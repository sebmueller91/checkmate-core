package checkmate.moves

import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState

internal abstract class PieceMoves {
    abstract fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move>
    fun generatePseudoLegalMoves(gameState: BitmapGameState, position: Position): List<Move> =
        generatePseudoLegalMoves(gameState).filter { it.from == position }
    abstract fun generateMoves(gameState: BitmapGameState): List<Move>
    fun generateMoves(gameState: BitmapGameState, position: Position): List<Move> =
        generateMoves(gameState).filter { it.from == position }
}