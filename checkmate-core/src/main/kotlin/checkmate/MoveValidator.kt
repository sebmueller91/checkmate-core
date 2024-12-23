package com.github.sebmu91.checkmate.checkmate

import com.github.sebmu91.checkmate.checkmate.model.GameState
import com.github.sebmu91.checkmate.checkmate.model.Move
import com.github.sebmu91.checkmate.checkmate.model.Position

interface MoveValidator {
    fun isValidMove(move: Move, gameState: GameState)
    fun getValidMoves(position: Position, gameState: GameState): List<Move>
}