package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.createMove
import checkmate.util.extractPositions
import checkmate.util.printAsBoard

internal fun BitmapGameState.generateRookMovesList(isWhiteTurn: Boolean): List<Move> {
    val rookBitmap = if (isWhiteTurn) whiteRooks else blackRooks
    return generateRookMoves(rookBitmap, isWhiteTurn)
}

private fun BitmapGameState.generateRookMoves(rookBitmap: ULong, isWhiteTurn: Boolean): List<Move> {
    val moves = mutableListOf<Move>()
    val opponentPieces = if (isWhiteTurn) blackPieces else whitePieces
    val occupied = allPieces

    opponentPieces.printAsBoard("opponentPieces")

    for (fromPos in extractPositions(rookBitmap)) {
        PrecomputedMovementMasks.straightMasks[fromPos].printAsBoard("position")
        val mask = PrecomputedMovementMasks.straightMasks[fromPos]

        val reachableSquares = calculateReachableSquares(mask, fromPos, occupied, opponentPieces)

        val validMoves = reachableSquares and (opponentPieces.inv())
        val captures = reachableSquares and opponentPieces

        moves.addAll(extractPositions(validMoves).map { toPos ->
            createMove(fromPos, toPos, null)
        })

        moves.addAll(extractPositions(captures).map { toPos ->
            createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
        })
    }

    return moves
}

private fun calculateReachableSquares(mask: ULong, fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
    val westRay = calculateRay(fromPos, step = -1, occupied, opponentPieces)
    val eastRay = calculateRay(fromPos, step = 1, occupied, opponentPieces)
    val northRay = calculateRay(fromPos, step = 8, occupied, opponentPieces)
    val southRay = calculateRay(fromPos, step = -8, occupied, opponentPieces)

    return (westRay or eastRay or northRay or southRay) and mask
}


private fun calculateRay(
    fromPos: Int,
    step: Int,
    occupied: ULong,
    opponentPieces: ULong
): ULong {
    var ray = 0UL
    var pos = fromPos + step

    while (pos in 0..63 && ((pos / 8 == fromPos / 8) || step % 8 == 0)) {
        val posBitmap = 1UL shl pos

        if ((posBitmap and occupied) != 0UL) {
            if ((posBitmap and opponentPieces) != 0UL) {
                ray = ray or posBitmap
            }
            break
        }

        ray = ray or posBitmap
        pos += step
    }

    return ray
}
