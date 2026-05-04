package dev.smueller.checkmate.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Client → Server ──────────────────────────────────────────────────────────

@Serializable
sealed class ClientMessage {
    @Serializable @SerialName("move")
    data class Move(val uci: String) : ClientMessage()

    @Serializable @SerialName("offer_draw")
    data object OfferDraw : ClientMessage()

    @Serializable @SerialName("accept_draw")
    data object AcceptDraw : ClientMessage()

    @Serializable @SerialName("decline_draw")
    data object DeclineDraw : ClientMessage()

    @Serializable @SerialName("resign")
    data object Resign : ClientMessage()

    @Serializable @SerialName("claim_draw")
    data class ClaimDraw(val rule: DrawClaimRule) : ClientMessage()

    @Serializable @SerialName("abort")
    data object Abort : ClientMessage()
}

@Serializable
enum class DrawClaimRule { FIFTY_MOVE, THREEFOLD }

@Serializable
data class ClockState(val white: Long, val black: Long)  // ms remaining

// ── Server → Client ──────────────────────────────────────────────────────────

@Serializable
sealed class ServerMessage {
    /** Full position snapshot after every move. Clients never diff — always replace. */
    @Serializable @SerialName("state")
    data class State(
        val fen: String,
        val lastMove: String?,   // UCI, null for initial snapshot
        val turn: String,        // "white" | "black"
        val status: String,      // "ongoing" | "checkmate" | "draw"
        val drawReason: String? = null,
        val winner: String? = null,
        val clocks: ClockState? = null,
    ) : ServerMessage()

    @Serializable @SerialName("draw_offered")
    data class DrawOffered(val by: String) : ServerMessage()

    @Serializable @SerialName("draw_declined")
    data object DrawDeclined : ServerMessage()

    @Serializable @SerialName("game_ended")
    data class GameEnded(val result: String, val reason: String) : ServerMessage()

    @Serializable @SerialName("error")
    data class Error(val message: String) : ServerMessage()

    @Serializable @SerialName("joined")
    data class Joined(val color: String, val inviteCode: String) : ServerMessage()

    @Serializable @SerialName("opponent_connected")
    data object OpponentConnected : ServerMessage()

    @Serializable @SerialName("opponent_disconnected")
    data object OpponentDisconnected : ServerMessage()
}
