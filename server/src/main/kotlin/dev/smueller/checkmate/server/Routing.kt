package dev.smueller.checkmate.server

import dev.smueller.checkmate.Color
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.header
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.util.UUID

private val routeJson = Json { encodeDefaults = true; ignoreUnknownKeys = true }

fun Application.configureWebSockets() {
    install(WebSockets)
}

fun Application.configureRouting() {
    val registry by inject<RoomRegistry>()
    val engineService by inject<EngineService>()
    val gameRepo: GameRepository? = runCatching { inject<GameRepository>().value }.getOrNull()

    routing {
        // ── Human vs Human ──────────────────────────────────────────────────

        webSocket("/game/new") {
            val userId = call.request.header("Authorization")?.extractBearerUserId()
            val timeMs = call.request.queryParameters["time"]?.toLongOrNull()?.times(1000)
            val incrementMs = call.request.queryParameters["increment"]?.toLongOrNull()?.times(1000) ?: 0L
            val clock = if (timeMs != null) ClockConfig(timeMs, incrementMs) else null
            val room = registry.create(clock)
            val color = room.join(this, userId)!!
            sendMsg(ServerMessage.Joined(color.name.lowercase(), room.inviteCode))
            room.sendSnapshot(this)
            serveGameRoom(room, registry, gameRepo)
        }

        webSocket("/game/{code}") {
            val code = call.parameters["code"] ?: run { close(); return@webSocket }
            val room = registry.find(code) ?: run {
                sendMsg(ServerMessage.Error("Room not found: $code"))
                close()
                return@webSocket
            }
            val userId = call.request.header("Authorization")?.extractBearerUserId()
            val color = room.join(this, userId) ?: run {
                sendMsg(ServerMessage.Error("Room is full"))
                close()
                return@webSocket
            }
            sendMsg(ServerMessage.Joined(color.name.lowercase(), code))
            room.sendSnapshot(this)
            room.notifyOpponentConnected(this)
            serveGameRoom(room, registry, gameRepo)
        }

        // ── Human vs Engine ─────────────────────────────────────────────────

        webSocket("/game/bot") {
            if (!engineService.available) {
                sendMsg(ServerMessage.Error("Engine not available — set STOCKFISH_PATH env var"))
                close()
                return@webSocket
            }
            val difficulty = Difficulty.fromString(
                call.request.queryParameters["difficulty"] ?: "medium"
            )
            val humanColor = when (call.request.queryParameters["side"]?.lowercase()) {
                "black" -> Color.BLACK
                else -> Color.WHITE
            }
            val room = BotRoom(humanColor, difficulty) { fen ->
                engineService.bestMove(fen, difficulty.movetimeMs, difficulty.skillLevel)
            }
            room.humanUserId = call.request.header("Authorization")?.extractBearerUserId()
            room.join(this)
            sendMsg(ServerMessage.Joined(humanColor.name.lowercase(), "bot:${difficulty.name.lowercase()}"))
            room.sendSnapshot(this)
            room.triggerBotMoveIfFirst()
            serveBotRoom(room, gameRepo)
        }
    }
}

// ── JWT helper ────────────────────────────────────────────────────────────────

private fun String.extractBearerUserId(): UUID? {
    val token = removePrefix("Bearer ").trim()
    return runCatching {
        UUID.fromString(JwtConfig.verifier.verify(token).getClaim("userId").asString())
    }.getOrNull()
}

// ── WebSocket helpers ────────────────────────────────────────────────────────

private suspend fun io.ktor.websocket.DefaultWebSocketSession.sendMsg(msg: ServerMessage) {
    outgoing.send(Frame.Text(routeJson.encodeToString(msg)))
}

private suspend fun io.ktor.websocket.DefaultWebSocketSession.serveGameRoom(
    room: GameRoom,
    registry: RoomRegistry,
    gameRepo: GameRepository?,
) {
    try {
        for (frame in incoming) {
            if (frame !is Frame.Text) continue
            val msg = routeJson.decodeFromString<ClientMessage>(frame.readText())
            room.handleMessage(this, msg)
        }
    } catch (_: ClosedReceiveChannelException) {
    } finally {
        room.leave(this)
        room.notifyOpponentDisconnected(this)
        room.takeGameEndRecord()?.let { record ->
            gameRepo?.save(record.movetext, record.result, record.whiteUserId, record.blackUserId)
            registry.remove(room.inviteCode)
            room.close()
        }
    }
}

private suspend fun io.ktor.websocket.DefaultWebSocketSession.serveBotRoom(
    room: BotRoom,
    gameRepo: GameRepository?,
) {
    try {
        for (frame in incoming) {
            if (frame !is Frame.Text) continue
            val msg = routeJson.runCatching {
                decodeFromString<ClientMessage>(frame.readText())
            }.getOrNull()
            if (msg == null) { sendMsg(ServerMessage.Error("Bad message format")); continue }
            room.handleMessage(this, msg)
        }
    } catch (_: ClosedReceiveChannelException) {
    } finally {
        room.leave()
        room.getGameEndRecord()?.let { record ->
            gameRepo?.save(record.movetext, record.result, record.whiteUserId, record.blackUserId)
        }
    }
}
