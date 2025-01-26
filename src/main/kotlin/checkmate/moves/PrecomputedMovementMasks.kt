internal object PrecomputedMovementMasks {
    val knightMasks = Array(64) { 0UL }
    val singleStepMasks = Array(64) { 0UL }

    init {
        calculateKnightMasks()
        calculateSingleStepMasks()
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
