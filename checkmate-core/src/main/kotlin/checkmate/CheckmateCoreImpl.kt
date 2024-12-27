package checkmate

import checkmate.model.Game
import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import toGameState

internal class CheckmateCoreImpl : CheckmateCore {
    override fun generateInitialState(): Game = Game(
        gameStates = listOf(BitmapGameState().apply { initializeStartingPosition() }.toGameState(lastMove = null))
    )


    override fun isValidMove(move: Move, gameState: GameState) {
        TODO("Not yet implemented")
    }

    override fun getValidMoves(position: Position, gameState: GameState): List<Move> {
        TODO("Not yet implemented")
    }

    override fun executeMove(move: Move, game: Game): Game {
        TODO("Not yet implemented")
    }

}