package checkmate.moves.type

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState

internal abstract class PieceMoves {
    abstract fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move>
    
    fun generatePseudoLegalMoves(gameState: BitmapGameState, position: Position): List<Move> =
        generatePseudoLegalMoves(gameState).filter { it.from == position }

    abstract fun generateLegalMoves(gameState: BitmapGameState): List<Move>

    fun generateLegalMoves(gameState: BitmapGameState, position: Position): List<Move> =
        generateLegalMoves(gameState).filter { it.from == position }

    abstract fun generateAttackMap(gameState: BitmapGameState, player: Player): ULong
}