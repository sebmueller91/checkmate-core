package dev.smueller.checkmate.server

import dev.smueller.checkmate.Color
import dev.smueller.checkmate.DrawReason
import dev.smueller.checkmate.GameStatus
import dev.smueller.checkmate.Move
import dev.smueller.checkmate.Position
import dev.smueller.checkmate.notation.Pgn
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.Duration
import java.util.UUID

private val roomJson = Json { encodeDefaults = true }
private val log = LoggerFactory.getLogger(GameRoom::class.java)

class GameRoom(val inviteCode: String, clockConfig: ClockConfig? = null) {

    private val mutex = Mutex()
    private val roomScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var position: Position = Position.initial
    private val positionHistory = mutableListOf<ULong>()
    private val moveHistory = mutableListOf<Move>()

    private var whiteSide: WebSocketSession? = null
    private var blackSide: WebSocketSession? = null
    var whiteUserId: UUID? = null
    var blackUserId: UUID? = null

    private val clock: GameClock? = clockConfig?.let { GameClock(it.initialMs, it.incrementMs) }
    private var flagJob: Job? = null

    private var drawOfferedBy: Color? = null
    private var moveCount = 0
    private var lastMoveAt: Instant = Instant.now()

    private var gameResult: String? = null
    private var gameReason: String? = null
    private var recordConsumed = false

    val isEmpty: Boolean get() = whiteSide == null && blackSide == null

    suspend fun join(session: WebSocketSession, userId: UUID? = null): Color? = mutex.withLock {
        return when {
            whiteSide == null -> { whiteSide = session; whiteUserId = userId; Color.WHITE }
            blackSide == null -> {
                blackSide = session; blackUserId = userId
                clock?.start()
                scheduleFlagCheck(Color.WHITE)
                Color.BLACK
            }
            else -> null
        }
    }

    suspend fun leave(session: WebSocketSession) = mutex.withLock {
        when (session) {
            whiteSide -> whiteSide = null
            blackSide -> blackSide = null
        }
    }

    fun colorOf(session: WebSocketSession): Color? =
        when (session) {
            whiteSide -> Color.WHITE
            blackSide -> Color.BLACK
            else -> null
        }

    suspend fun handleMessage(sender: WebSocketSession, msg: ClientMessage) = mutex.withLock {
        val color = colorOf(sender) ?: return@withLock
        when (msg) {
            is ClientMessage.Move -> handleMove(sender, color, msg.uci)
            is ClientMessage.Resign -> handleResign(color)
            is ClientMessage.OfferDraw -> handleOfferDraw(sender, color)
            is ClientMessage.AcceptDraw -> handleAcceptDraw(color)
            is ClientMessage.DeclineDraw -> handleDeclineDraw(sender)
            is ClientMessage.ClaimDraw -> handleClaimDraw(sender, color, msg.rule)
            is ClientMessage.Abort -> handleAbort(color)
        }
    }

    private suspend fun handleMove(sender: WebSocketSession, color: Color, uci: String) {
        if (gameResult != null) { sender.sendMsg(ServerMessage.Error("Game already over")); return }
        if (position.sideToMove != color) { sender.sendMsg(ServerMessage.Error("Not your turn")); return }
        val move = position.legalMoves().firstOrNull { it.toUci() == uci }
        if (move == null) { sender.sendMsg(ServerMessage.Error("Illegal move: $uci")); return }

        val now = Instant.now()
        val moveTimeMs = Duration.between(lastMoveAt, now).toMillis()
        lastMoveAt = now

        val remaining = clock?.recordMove(color)
        if (remaining != null && remaining <= 0) {
            flagJob?.cancel()
            log.info("FLAG game={} loser={} moveTimeMs={}", inviteCode, color.name.lowercase(), moveTimeMs)
            endGame(color.opposite().name.lowercase(), "timeout")
            broadcast(ServerMessage.GameEnded(color.opposite().name.lowercase(), "timeout"))
            return
        }

        positionHistory.add(position.zobristHash)
        moveHistory.add(move)
        position = position.makeMove(move)
        moveCount++
        drawOfferedBy = null

        log.info("MOVE game={} n={} color={} uci={} moveTimeMs={} remaining={}",
            inviteCode, moveCount, color.name.lowercase(), uci, moveTimeMs,
            clock?.remaining(color) ?: -1L)

        val status = position.statusWithHistory(positionHistory)
        broadcast(stateMessage(uci, status))
        if (status != GameStatus.Ongoing) {
            endGame(status)
            broadcastGameEnd(status)
        } else {
            scheduleFlagCheck(position.sideToMove)
        }
    }

    private suspend fun handleResign(color: Color) {
        val winner = color.opposite().name.lowercase()
        endGame(winner, "resign")
        broadcast(ServerMessage.GameEnded(winner, "resign"))
    }

    private suspend fun handleOfferDraw(sender: WebSocketSession, color: Color) {
        drawOfferedBy = color
        opponent(sender)?.sendMsg(ServerMessage.DrawOffered(color.name.lowercase()))
    }

    private suspend fun handleAcceptDraw(color: Color) {
        if (drawOfferedBy != null && drawOfferedBy != color) {
            endGame("draw", "agreement")
            broadcast(ServerMessage.GameEnded("draw", "agreement"))
        }
    }

    private suspend fun handleDeclineDraw(sender: WebSocketSession) {
        drawOfferedBy = null
        opponent(sender)?.sendMsg(ServerMessage.DrawDeclined)
    }

    private suspend fun handleClaimDraw(sender: WebSocketSession, color: Color, rule: DrawClaimRule) {
        val status = position.statusWithHistory(positionHistory)
        val valid = when (rule) {
            DrawClaimRule.FIFTY_MOVE -> status == GameStatus.Draw(DrawReason.FIFTY_MOVE_RULE)
            DrawClaimRule.THREEFOLD -> status == GameStatus.Draw(DrawReason.THREEFOLD_REPETITION)
        }
        if (valid) {
            endGame("draw", rule.name.lowercase())
            broadcast(ServerMessage.GameEnded("draw", rule.name.lowercase()))
        } else {
            sender.sendMsg(ServerMessage.Error("Draw claim invalid"))
        }
    }

    private suspend fun handleAbort(color: Color) {
        if (moveCount < 2) {
            endGame("aborted", "abort")
            broadcast(ServerMessage.GameEnded("aborted", "abort"))
        } else {
            sessionOf(color)?.sendMsg(ServerMessage.Error("Abort only allowed before move 2"))
        }
    }

    suspend fun sendSnapshot(session: WebSocketSession) = mutex.withLock {
        val result = gameResult
        val reason = gameReason
        session.sendMsg(stateMessage(null, position.statusWithHistory(positionHistory)))
        if (result != null && reason != null) {
            session.sendMsg(ServerMessage.GameEnded(result, reason))
        }
    }

    suspend fun notifyOpponentConnected(session: WebSocketSession) {
        opponent(session)?.sendMsg(ServerMessage.OpponentConnected)
    }

    suspend fun notifyOpponentDisconnected(session: WebSocketSession) {
        opponent(session)?.sendMsg(ServerMessage.OpponentDisconnected)
    }

    /** Returns the end record exactly once (first caller after game over). */
    fun takeGameEndRecord(): GameEndRecord? = synchronized(this) {
        if (recordConsumed || gameResult == null) return null
        recordConsumed = true
        GameEndRecord(
            movetext = Pgn.formatMovetext(Position.initial, moveHistory),
            result = gameResult!!,
            whiteUserId = whiteUserId,
            blackUserId = blackUserId,
        )
    }

    fun close() { roomScope.cancel() }

    // ── Private helpers ───────────────────────────────────────────────────────

    private fun endGame(result: String, reason: String) {
        gameResult = result
        gameReason = reason
        flagJob?.cancel()
        flagJob = null
    }

    private fun endGame(status: GameStatus) = when (status) {
        is GameStatus.Checkmate -> endGame(status.winner.name.lowercase(), "checkmate")
        is GameStatus.Draw -> endGame("draw", status.reason.name.lowercase())
        is GameStatus.Ongoing -> Unit
    }

    private fun scheduleFlagCheck(color: Color) {
        flagJob?.cancel()
        val remaining = clock?.remaining(color) ?: return
        if (remaining <= 0) return
        flagJob = roomScope.launch {
            delay(remaining)
            mutex.withLock {
                if (gameResult == null && position.sideToMove == color) {
                    log.info("AUTO-FLAG game={} loser={}", inviteCode, color.name.lowercase())
                    endGame(color.opposite().name.lowercase(), "timeout")
                    broadcast(ServerMessage.GameEnded(color.opposite().name.lowercase(), "timeout"))
                }
            }
        }
    }

    private suspend fun broadcast(msg: ServerMessage) {
        whiteSide?.sendMsg(msg)
        blackSide?.sendMsg(msg)
    }

    private fun opponent(session: WebSocketSession): WebSocketSession? =
        if (session == whiteSide) blackSide else whiteSide

    private fun sessionOf(color: Color): WebSocketSession? =
        if (color == Color.WHITE) whiteSide else blackSide

    private fun stateMessage(lastMove: String?, status: GameStatus): ServerMessage.State {
        val statusStr = when (status) {
            is GameStatus.Ongoing -> "ongoing"
            is GameStatus.Checkmate -> "checkmate"
            is GameStatus.Draw -> "draw"
        }
        return ServerMessage.State(
            fen = position.toFen(),
            lastMove = lastMove,
            turn = position.sideToMove.name.lowercase(),
            status = statusStr,
            drawReason = (status as? GameStatus.Draw)?.reason?.name?.lowercase(),
            winner = (status as? GameStatus.Checkmate)?.winner?.name?.lowercase(),
            clocks = clock?.let { ClockState(it.whiteMs, it.blackMs) },
        )
    }

    private suspend fun broadcastGameEnd(status: GameStatus) {
        val msg = when (status) {
            is GameStatus.Checkmate -> ServerMessage.GameEnded(status.winner.name.lowercase(), "checkmate")
            is GameStatus.Draw -> ServerMessage.GameEnded("draw", status.reason.name.lowercase())
            is GameStatus.Ongoing -> return
        }
        broadcast(msg)
    }
}

private suspend fun WebSocketSession.sendMsg(msg: ServerMessage) {
    outgoing.send(Frame.Text(roomJson.encodeToString(msg)))
}
