package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.createMove
import checkmate.util.extractPositions

internal fun BitmapGameState.generateKnightMoves(isWhiteTurn: Boolean): List<Move> {
    val knights = if (isWhiteTurn) whiteKnights else blackKnights
    val opponents = if (isWhiteTurn) blackPieces else whitePieces

    val moves = mutableListOf<Move>()
    for (fromPos in extractPositions(knights)) {
        val mask = PrecomputedMovementMasks.knightMasks[fromPos]

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