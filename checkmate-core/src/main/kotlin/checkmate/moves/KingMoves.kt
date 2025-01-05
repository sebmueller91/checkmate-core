package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object KingMoves : PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val king = if (gameState.isWhiteTurn) gameState.whiteKing else gameState.blackKing
        val opponents = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces

        val moves = mutableListOf<Move>()
        for (fromPos in extractPositions(king)) {
            val mask = PrecomputedMovementMasks.singleStepMasks[fromPos]

            val validMoves = mask and gameState.allPieces.inv()
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

    override fun generateMoves(gameState: BitmapGameState): List<Move> {
        TODO("Not yet implemented")
    }

    override fun getAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val king = if (player == Player.WHITE) gameState.whiteKing else gameState.blackKing
        val opponents = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces

        var attackMask = 0UL
        for (fromPos in extractPositions(king)) {
            val mask = PrecomputedMovementMasks.singleStepMasks[fromPos]

            attackMask = attackMask or (mask and gameState.allPieces.inv())
            attackMask = attackMask or (mask and opponents)
        }
        return attackMask
    }
}