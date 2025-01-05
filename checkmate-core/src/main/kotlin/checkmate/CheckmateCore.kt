package checkmate

import checkmate.model.*
import checkmate.moves.*
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

    fun getValidMoves(gameState: GameState): List<Move> {
        TODO("Not yet implemented")
    }

    fun getValidMoves(gameState: GameState, position: Position): List<Move> {
        if (position.rank !in 0..7 || position.file !in 0..7) {
            // TODO: Throw exception
            return listOf()
        }

        if (gameState.currentPlayer != gameState.board[position.rank][position.file]?.color) {
            return listOf()
        }

        val bitmapGameState = gameState.toBitmapGameState()
        val pieceType = gameState.board[position.rank][position.file]?.type ?: return listOf()
        val pieceMoves = getPieceMoves(pieceType)

        return pieceMoves.generateMoves(bitmapGameState, position)
    }

    fun executeMove(move: Move, game: Game): Game {
        TODO("Not yet implemented")
    }

    private fun getPieceMoves(type: Type) = when(type) {
        Type.PAWN -> PawnMoves
        Type.KNIGHT -> KnightMoves
        Type.BISHOP -> BishopMoves
        Type.ROOK -> RookMoves
        Type.QUEEN -> QueenMoves
        Type.KING -> KingMoves
    }
}