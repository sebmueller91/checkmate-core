package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object KingMoves : PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> = gameState.getPseudoLegalMoves()

    override fun generateMoves(gameState: BitmapGameState): List<Move> {
        TODO("Not yet implemented")
    }

    private fun BitmapGameState.getPseudoLegalMoves(): List<Move> {
        val king = if (isWhiteTurn) whiteKing else blackKing
        val opponents = if (isWhiteTurn) blackPieces else whitePieces

        val moves = mutableListOf<Move>()
        for (fromPos in extractPositions(king)) {
            val mask = PrecomputedMovementMasks.singleStepMasks[fromPos]

            val validMoves = mask and allPieces.inv()
            moves.addAll(extractPositions(validMoves).map { toPos ->
                createMove(fromPos, toPos, null)
            })

            val captures = mask and opponents
            moves.addAll(extractPositions(captures).map { toPos ->
                createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
            })
        }
        return moves
    }
}