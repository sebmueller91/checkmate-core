package checkmate

import checkmate.model.*
import checkmate.moves.generatePawnMovesList
import checkmate.moves.generateRookMovesList
import checkmate.moves.model.BitmapGameState
import checkmate.util.toBitmapGameState
import toGameState

class CheckmateCore {
    fun generateInitialState(): Game = Game(
        gameStates = listOf(BitmapGameState().apply { initializeStartingPosition() }.toGameState(lastMove = null))
    )

    fun isValidMove(move: Move, gameState: GameState) {
        TODO("Not yet implemented")
    }

    fun getValidMoves(position: Position, gameState: GameState): List<Move> {
        if (position.rank !in 0..7 || position.file !in 0..7) {
            // TODO: Throw exception
            return listOf()
        }

        if (gameState.currentPlayer != gameState.board[position.rank][position.file]?.color) {
            return listOf()
        }

        val bitmapGameState = gameState.toBitmapGameState()
        val movesList = when (gameState.board[position.rank][position.file]?.type) {
            null -> listOf()
            Type.PAWN -> bitmapGameState.generatePawnMovesList(isWhiteTurn = gameState.currentPlayer == Player.WHITE)
            Type.KNIGHT -> TODO()
            Type.BISHOP -> TODO()
            Type.ROOK -> bitmapGameState.generateRookMovesList(isWhiteTurn = gameState.currentPlayer == Player.WHITE)
            Type.QUEEN -> TODO()
            Type.KING -> TODO()
        }
        return movesList.filter { it.from == position }
    }

    fun executeMove(move: Move, game: Game): Game {
        TODO("Not yet implemented")
    }
}