package com.github.sebmu91.checkmate.checkmate.model

data class GameState(
    val board: List<List<Piece?>>,
    val currentPlayer: Player,
    val moveHistory: List<Move>,
    val gameStatus: GameStatus
)
