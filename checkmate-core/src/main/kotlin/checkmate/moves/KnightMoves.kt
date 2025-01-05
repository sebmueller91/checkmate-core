package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object KnightMoves : PieceMoves() {

    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val knights = if (gameState.isWhiteTurn) gameState.whiteKnights else gameState.blackKnights
        val opponents = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces

        val moves = mutableListOf<Move>()
        for (fromPos in extractPositions(knights)) {
            val mask = PrecomputedMovementMasks.knightMasks[fromPos]

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
        val knights = if (player == Player.WHITE) gameState.whiteKnights else gameState.blackKnights
        val opponents = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces

        var attackMap = 0UL
        for (fromPos in extractPositions(knights)) {
            val mask = PrecomputedMovementMasks.knightMasks[fromPos]

            attackMap = attackMap or (mask and gameState.allPieces.inv())
            attackMap = attackMap or (mask and opponents)
        }
        return attackMap
    }
}