package checkmate.moves

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.moves.model.FILE_MASK
import checkmate.util.extractPositions

internal fun BitmapGameState.generateRookMovesList(isWhiteTurn: Boolean): List<Move> {
    val rookBitmap = if (isWhiteTurn) whiteRooks else blackRooks
    return generateRookMoves(rookBitmap, isWhiteTurn)
}

private fun BitmapGameState.generateRookMoves(rookBitmap: ULong, isWhiteTurn: Boolean): List<Move> {
    val moves = mutableListOf<Move>()
    val opponentPieces = if (isWhiteTurn) blackPieces else whitePieces
    val occupied = allPieces

    for (fromPos in extractPositions(rookBitmap)) {
        val mask = PrecomputedMovementMasks.straightMasks[fromPos]

        // Calculate reachable squares
        val reachableSquares = calculateReachableSquares(mask, fromPos, occupied)

        val validMoves = reachableSquares and (opponentPieces.inv()) // Exclude opponent pieces for regular moves
        val captures = reachableSquares and opponentPieces           // Include only opponent pieces for captures

        // Generate moves for empty squares
        moves.addAll(extractPositions(validMoves).map { toPos ->
            createMove(fromPos, toPos, null)
        })

        // Generate moves for capture squares
        moves.addAll(extractPositions(captures).map { toPos ->
            createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
        })
    }

    return moves
}

private fun calculateReachableSquares(mask: ULong, fromPos: Int, occupied: ULong): ULong {
    val rank = fromPos / 8
    val file = fromPos % 8

    // Blockers in all directions
    val horizontalBlockers = mask and occupied and (0xFFUL shl (rank * 8))
    val verticalBlockers = mask and occupied and FILE_MASK[file]

    // Nearest blockers (west, east, north, south)
    val westBlocker = (horizontalBlockers and (0xFFFFFFFFFFFFFFFFUL shl fromPos)).takeLowestSetBit()
    val eastBlocker = (horizontalBlockers and (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))).takeHighestSetBit()
    val northBlocker = (verticalBlockers and (0xFFFFFFFFFFFFFFFFUL shl fromPos)).takeLowestSetBit()
    val southBlocker = (verticalBlockers and (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))).takeHighestSetBit()

    // Calculate valid rays by stopping at blockers
    val westRay = if (westBlocker != 0UL) (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos)) and (0xFFFFFFFFFFFFFFFFUL shl westBlocker.bitPosition()) else (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))
    val eastRay = if (eastBlocker != 0UL) (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - eastBlocker.bitPosition())) and (0xFFFFFFFFFFFFFFFFUL shl fromPos) else (0xFFFFFFFFFFFFFFFFUL shl fromPos)
    val northRay = if (northBlocker != 0UL) (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos)) and (0xFFFFFFFFFFFFFFFFUL shl northBlocker.bitPosition()) else (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - fromPos))
    val southRay = if (southBlocker != 0UL) (0xFFFFFFFFFFFFFFFFUL.unsignedShr(63 - southBlocker.bitPosition())) and (0xFFFFFFFFFFFFFFFFUL shl fromPos) else (0xFFFFFFFFFFFFFFFFUL shl fromPos)

    return (westRay or eastRay or northRay or southRay) and mask
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
