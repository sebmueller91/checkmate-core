package checkmate.util

import checkmate.model.Move
import checkmate.model.Position

internal fun extractPositions(bitboard: ULong): List<Int> {
    return generateSequence(bitboard) { it and (it - 1UL) } // Iteratively clear the least significant bit
        .takeWhile { it != 0UL } // Stop when no bits are set
        .map { it.countTrailingZeroBits() } // Map each isolated bit to its index
        .toList() // Convert the sequence to a list
}

internal fun Position.toBitboard(): ULong = 1UL shl (rank * 8 + file)

internal fun createMove(fromPos: Int, toPos: Int, capture: Position? = null, castlingRookFromTo: Pair<Position, Position>? = null): Move {
    val fromRank = fromPos / 8
    val fromFile = fromPos % 8
    val toRank = toPos / 8
    val toFile = toPos % 8

    return Move(
        from = Position(rank = fromRank, file = fromFile),
        to = Position(rank = toRank, file = toFile),
        capture = capture,
        castlingRookFromTo = castlingRookFromTo
    )
}

internal fun ULong.printAsBoard(title: String = "") {
    if (title.isNotBlank()) println(title)
    println("  0 1 2 3 4 5 6 7")
    for (rank in 7 downTo 0) {
        print("$rank ")
        for (file in 0..7) {
            val position = rank * 8 + file
            if ((this and (1UL shl position)) != 0UL) {
                print("# ")
            } else {
                print(". ")
            }
        }
        println(" $rank")
    }
    println("  0 1 2 3 4 5 6 7")
    println()
}

internal fun calculateStraightRay(
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

internal fun calculateDiagonalRay(
    fromPos: Int,
    step: Int,
    occupied: ULong,
    opponentPieces: ULong
): ULong {
    var ray = 0UL
    var pos = fromPos + step

    while (pos in 0..63 && isValidDiagonalMove(fromPos, pos, step)) {
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

private fun isValidDiagonalMove(fromPos: Int, toPos: Int, step: Int): Boolean {
    val fromFile = fromPos % 8
    val toFile = toPos % 8
    val toRank = toPos / 8

    return when (step) {
        -7, 9 -> toFile > fromFile
        -9, 7 -> toFile < fromFile
        else -> false
    } && (toRank in 0..7)
}