package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.moves.model.FILE_MASKS
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
    val rank = fromPos / 8
    val file = fromPos % 8

    // Blockers in all directions
    val horizontalBlockers = mask and occupied and (0xFFUL shl (rank * 8))
    val verticalBlockers = mask and occupied and FILE_MASKS[file]

    // Nearest blockers (west, east, north, south)
    val westBlocker = (horizontalBlockers and (0xFFFFFFFFFFFFFFFFUL shl fromPos)).takeLowestSetBit()
    val eastBlocker = (horizontalBlockers and (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))).takeHighestSetBit()
    val northBlocker = (verticalBlockers and (0xFFFFFFFFFFFFFFFFUL shl fromPos)).takeLowestSetBit()
    val southBlocker = (verticalBlockers and (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))).takeHighestSetBit()

    // Calculate valid rays
    val westRay = calculateRay(fromPos, westBlocker, isPositiveDirection = false, opponentPieces, occupied)
    val eastRay = calculateRay(fromPos, eastBlocker, isPositiveDirection = true, opponentPieces, occupied)
    val northRay = calculateRay(fromPos, northBlocker, isPositiveDirection = false, opponentPieces, occupied, isVertical = true)
    val southRay = calculateRay(fromPos, southBlocker, isPositiveDirection = true, opponentPieces, occupied, isVertical = true)

    return (westRay or eastRay or northRay or southRay) and mask
}

private fun calculateRay(
    fromPos: Int,
    blocker: ULong,
    isPositiveDirection: Boolean,
    opponentPieces: ULong,
    occupied: ULong,
    isVertical: Boolean = false
): ULong {
    if (blocker == 0UL) return 0xFFFFFFFFFFFFFFFFUL // No blockers, full ray

    val blockerPos = blocker.bitPosition()
    val ray = if (isPositiveDirection) {
        val shift = if (isVertical) 8 else 1
        (0xFFFFFFFFFFFFFFFFUL shl fromPos) and (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - blockerPos))
    } else {
        val shift = if (isVertical) -8 else -1
        (0xFFFFFFFFFFFFFFFFUL.unsignedShr(fromPos)) and (0xFFFFFFFFFFFFFFFFUL shl blockerPos)
    }

    return if (opponentPieces and blocker != 0UL) ray else ray and blocker.inv()
}


private fun ULong.unsignedShr(bits: Int): ULong = (this shr bits) and (ULong.MAX_VALUE shr bits)
private fun ULong.takeHighestSetBit(): ULong = this.takeLowestSetBit().rotateRight(63)
private fun ULong.takeLowestSetBit(): ULong {
    return this and (this.inv() + 1UL)
}

private fun ULong.bitPosition(): Int {
    return this.countTrailingZeroBits()
}

private fun createMove(fromPos: Int, toPos: Int, capture: Position?): Move {
    val fromRank = fromPos / 8
    val fromFile = fromPos % 8
    val toRank = toPos / 8
    val toFile = toPos % 8

    return Move(
        from = Position(rank = fromRank, file = fromFile),
        to = Position(rank = toRank, file = toFile),
        capture = capture
    )
}
