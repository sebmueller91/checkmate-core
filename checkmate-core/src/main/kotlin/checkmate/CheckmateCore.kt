package checkmate

import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position

interface CheckmateCore {
    fun generateInitialState(): GameState
    fun isValidMove(move: Move, gameState: GameState)
    fun getValidMoves(position: Position, gameState: GameState): List<Move>
    fun executeMove(move: Move, gameState: GameState): GameState
}