import checkmate.moves.model.FILE_MASKS
import checkmate.moves.model.RANK_MASKS

internal object PrecomputedMovementMasks {

    val straightMasks = Array(64) { 0UL }
    val diagonalMasks = Array(64) { 0UL }
    val knightMasks = Array(64) { 0UL }
    val singleStepMasks = Array(64) { 0UL }

    init {
        calculateStraightMasks()
        calculateDiagonalMasks()
        calculateKnightMasks()
        calculateSingleStepMasks()
    }

    private fun calculateStraightMasks() {
        for (square in 0..63) {
            val rank = square / 8
            val file = square % 8

            straightMasks[square] = (RANK_MASKS[rank] or FILE_MASKS[file]) and (1UL shl square).inv()
        }
    }

    private fun calculateDiagonalMasks() {
        for (square in 0..63) {
            val rank = square / 8
            val file = square % 8

            var mainDiagonalMask = 0UL
            var antiDiagonalMask = 0UL

            // Main diagonal: top-left to bottom-right
            for (d in -7..7) {
                val r = rank + d
                val f = file + d
                if (r in 0..7 && f in 0..7) {
                    mainDiagonalMask = mainDiagonalMask or (1UL shl (r * 8 + f))
                }
            }

            // Anti-diagonal: top-right to bottom-left
            for (d in -7..7) {
                val r = rank + d
                val f = file - d
                if (r in 0..7 && f in 0..7) {
                    antiDiagonalMask = antiDiagonalMask or (1UL shl (r * 8 + f))
                }
            }

            // Combine diagonals, excluding the current square
            diagonalMasks[square] = (mainDiagonalMask or antiDiagonalMask) and (1UL shl square).inv()
        }
    }

    private fun calculateKnightMasks() {
        for (square in 0..63) {
            val rank = square / 8
            val file = square % 8

            var knightMask = 0UL

            val moves = listOf(
                Pair(-2, -1), Pair(-2, 1),
                Pair(-1, -2), Pair(-1, 2),
                Pair(1, -2), Pair(1, 2),
                Pair(2, -1), Pair(2, 1)
            )

            for ((dr, df) in moves) {
                val r = rank + dr
                val f = file + df
                if (r in 0..7 && f in 0..7) {
                    knightMask = knightMask or (1UL shl (r * 8 + f))
                }
            }

            knightMasks[square] = knightMask
        }
    }

    private fun calculateSingleStepMasks() {
        for (square in 0..63) {
            val rank = square / 8
            val file = square % 8

            var kingMask = 0UL

            val moves = listOf(
                Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
                Pair(0, -1), Pair(0, 1),
                Pair(1, -1), Pair(1, 0), Pair(1, 1)
            )

            for ((dr, df) in moves) {
                val r = rank + dr
                val f = file + df
                if (r in 0..7 && f in 0..7) {
                    kingMask = kingMask or (1UL shl (r * 8 + f))
                }
            }

            singleStepMasks[square] = kingMask
        }
    }
}
