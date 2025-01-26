package checkmate

import checkmate.exception.InvalidMoveException
import checkmate.exception.InvalidPositionException
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.moves.model.BitmapGameState
import checkmate.moves.type.*
import checkmate.util.toBitmapGameState
import toGameState
import java.security.InvalidParameterException

internal class CheckmateCoreImpl: CheckmateCore {
    override fun getInitialGame(): Game = Game(
        gameStates = listOf(BitmapGameState().apply { initializeStartingPosition() }.toGameState(lastMove = null))
    )

    override fun getValidMoves(gameState: GameState): List<Move> {
        val validMoves = mutableListOf<Move>()
        for (rank in 0..7) {
            for (file in 0..7) {
                val piece = gameState.board[rank][file] ?: continue
                if (piece.color != gameState.currentPlayer) {
                    continue
                }

                validMoves.addAll(getValidMoves(gameState, Position(rank, file)))
            }
        }
        return validMoves
    }

    override fun isValidMove(gameState: GameState, move: Move): Boolean = // TODO: Test
        move in getValidMoves(gameState)

    override fun getValidMoves(gameState: GameState, position: Position): List<Move> {
        if (position.rank !in 0..7 || position.file !in 0..7) {
            throw InvalidPositionException("Position $position is not valid.")
        }

        if (gameState.currentPlayer != gameState.board[position.rank][position.file]?.color) {
            return listOf()
        }

        val bitmapGameState = gameState.toBitmapGameState()
        val pieceType = gameState.board[position.rank][position.file]?.type ?: return listOf()
        val pieceMoves = getPieceMoves(pieceType)

        return pieceMoves.generateLegalMoves(bitmapGameState, position)
    }

    override fun executeMove(move: Move, game: Game, moveIndex: Int?): Game { // TODO: Test
        if (moveIndex != null && moveIndex !in game.gameStates.indices) {
            throw InvalidParameterException("Can not execute move at index $moveIndex. Valid indices are between 0 and ${game.gameStates.size - 1}.")
        }
        val toIndex = moveIndex ?: (game.gameStates.size - 1)
        val curGameState = game.gameStates[toIndex]
        if (!isValidMove(curGameState, move)) {
            throw InvalidMoveException("Can not execute move because it is not valid. Move: $move")
        }
        // TODO: Remove print
        val newGameState = curGameState.toBitmapGameState().executeMove(move).toGameState(move)
        return game.copy(gameStates = game.gameStates.subList(0, toIndex+1) + newGameState)
    }

    private fun getPieceMoves(type: Type) = when (type) {
        Type.PAWN -> PawnMoves
        Type.KNIGHT -> KnightMoves
        Type.BISHOP -> BishopMoves
        Type.ROOK -> RookMoves
        Type.QUEEN -> QueenMoves
        Type.KING -> KingMoves
    }
}