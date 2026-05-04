package dev.smueller.checkmate

sealed class GameStatus {
    data object Ongoing : GameStatus()
    data class Checkmate(val winner: Color) : GameStatus()
    data class Draw(val reason: DrawReason) : GameStatus()
}

enum class DrawReason {
    STALEMATE,
    FIFTY_MOVE_RULE,
    THREEFOLD_REPETITION,
    INSUFFICIENT_MATERIAL,
}
