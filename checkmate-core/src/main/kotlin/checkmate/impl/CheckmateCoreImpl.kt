package checkmate.impl

import checkmate.CheckmateCore
import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position

internal class CheckmateCoreImpl: CheckmateCore {
    override fun generateInitialState(): GameState {
        TODO("Not yet implemented")
    }
    
    override fun isValidMove(move: Move, gameState: GameState) {
        TODO("Not yet implemented")
    }

    override fun getValidMoves(position: Position, gameState: GameState): List<Move> {
        TODO("Not yet implemented")
    }

    override fun executeMove(move: Move, gameState: GameState): GameState {
        TODO("Not yet implemented")
    }
}