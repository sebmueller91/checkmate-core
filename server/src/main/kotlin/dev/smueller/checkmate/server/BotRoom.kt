package dev.smueller.checkmate.server

import dev.smueller.checkmate.Color
import dev.smueller.checkmate.GameStatus
import dev.smueller.checkmate.Move
import dev.smueller.checkmate.Position
import dev.smueller.checkmate.notation.Pgn
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

private val botJson = Json { encodeDefaults = true }

class BotRoom(
    val humanColor: Color,
    val difficulty: Difficulty,
    private val getMove: suspend (fen: String) -> String,
) {
    val botColor: Color = humanColor.opposite()

    private val mutex = Mutex()
    private var position = Position.initial
    private val positionHistory = mutableListOf<ULong>()
    private val moveHistory = mutableListOf<Move>()
    private var session: WebSocketSession? = null
    private var gameResult: String? = null
    var humanUserId: UUID? = null

    suspend fun join(ws: WebSocketSession) = mutex.withLock { session = ws }
    suspend fun leave() = mutex.withLock { session = null }

    suspend fun sendSnapshot(ws: WebSocketSession) {
        ws.sendMsg(stateMessage(null, mutex.withLock {
            position.statusWithHistory(positionHistory)
        }))
    }

    suspend fun triggerBotMoveIfFirst() {
        if (position.sideToMove == botColor) doBotMove()
    }

    suspend fun handleMessage(sender: WebSocketSession, msg: ClientMessage) {
        val needsBotMove = mutex.withLock { applyHumanMessage(sender, msg) }
        if (needsBotMove) doBotMove()
    }

    fun getGameEndRecord(): GameEndRecord? {
        val result = gameResult ?: return null
        return GameEndRecord(
            movetext = Pgn.formatMovetext(Position.initial, moveHistory),
            result = result,
            whiteUserId = if (humanColor == Color.WHITE) humanUserId else null,
            blackUserId = if (humanColor == Color.BLACK) humanUserId else null,
        )
    }

    private suspend fun applyHumanMessage(sender: WebSocketSession, msg: ClientMessage): Boolean {
        return when (msg) {
            is ClientMessage.Move -> applyHumanMove(sender, msg.uci)
            is ClientMessage.Resign -> {
                val winner = botColor.name.lowercase()
                gameResult = winner
                sender.sendMsg(ServerMessage.GameEnded(winner, "resign"))
                false
            }
            else -> false
        }
    }

    private suspend fun applyHumanMove(sender: WebSocketSession, uci: String): Boolean {
        if (position.sideToMove != humanColor) {
            sender.sendMsg(ServerMessage.Error("Not your turn"))
            return false
        }
        val move = position.legalMoves().firstOrNull { it.toUci() == uci }
        if (move == null) {
            sender.sendMsg(ServerMessage.Error("Illegal move: $uci"))
            return false
        }
        positionHistory.add(position.zobristHash)
        moveHistory.add(move)
        position = position.makeMove(move)
        val status = position.statusWithHistory(positionHistory)
        sender.sendMsg(stateMessage(uci, status))
        if (status != GameStatus.Ongoing) {
            gameResult = endResultString(status)
            sender.sendMsg(gameEndedMessage(status))
            return false
        }
        return position.sideToMove == botColor
    }

    private suspend fun doBotMove() {
        val fen = mutex.withLock { position.toFen() }
        val uci = runCatching { getMove(fen) }.getOrElse { e ->
            mutex.withLock { session }?.sendMsg(ServerMessage.Error("Engine error: ${e.message}"))
            return
        }
        mutex.withLock {
            val move = position.legalMoves().firstOrNull { it.toUci() == uci }
            if (move == null) {
                session?.sendMsg(ServerMessage.Error("Engine returned illegal move: $uci"))
                return@withLock
            }
            positionHistory.add(position.zobristHash)
            moveHistory.add(move)
            position = position.makeMove(move)
            val status = position.statusWithHistory(positionHistory)
            session?.sendMsg(stateMessage(uci, status))
            if (status != GameStatus.Ongoing) {
                gameResult = endResultString(status)
                session?.sendMsg(gameEndedMessage(status))
            }
        }
    }

    private fun endResultString(status: GameStatus): String = when (status) {
        is GameStatus.Checkmate -> status.winner.name.lowercase()
        is GameStatus.Draw -> "draw"
        is GameStatus.Ongoing -> error("game still ongoing")
    }

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
        )
    }

    private fun gameEndedMessage(status: GameStatus): ServerMessage.GameEnded = when (status) {
        is GameStatus.Checkmate ->
            ServerMessage.GameEnded(status.winner.name.lowercase(), "checkmate")
        is GameStatus.Draw ->
            ServerMessage.GameEnded("draw", status.reason.name.lowercase())
        is GameStatus.Ongoing -> error("game still ongoing")
    }
}

private suspend fun WebSocketSession.sendMsg(msg: ServerMessage) {
    outgoing.send(Frame.Text(botJson.encodeToString(msg)))
}
