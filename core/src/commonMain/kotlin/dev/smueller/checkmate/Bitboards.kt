package dev.smueller.checkmate

/**
 * Bitboard utilities. Bit i corresponds to square i (a1 = 0, h1 = 7, a8 = 56, h8 = 63).
 *
 * Sliders use simple ray iteration ("classical" approach). Magic bitboards are an
 * intentional v0.x optimization once perft is green; ray-based is obviously correct,
 * and that's what matters first.
 */
internal object Bitboards {

    // --- File / rank masks -------------------------------------------------

    const val FILE_A: ULong = 0x0101010101010101uL
    val FILE_B: ULong = FILE_A shl 1
    val FILE_C: ULong = FILE_A shl 2
    val FILE_D: ULong = FILE_A shl 3
    val FILE_E: ULong = FILE_A shl 4
    val FILE_F: ULong = FILE_A shl 5
    val FILE_G: ULong = FILE_A shl 6
    val FILE_H: ULong = FILE_A shl 7

    val NOT_FILE_A: ULong = FILE_A.inv()
    val NOT_FILE_H: ULong = FILE_H.inv()
    val NOT_FILE_AB: ULong = (FILE_A or FILE_B).inv()
    val NOT_FILE_GH: ULong = (FILE_G or FILE_H).inv()

    const val RANK_1: ULong = 0x00000000000000FFuL
    val RANK_2: ULong = RANK_1 shl 8
    val RANK_3: ULong = RANK_1 shl 16
    val RANK_4: ULong = RANK_1 shl 24
    val RANK_5: ULong = RANK_1 shl 32
    val RANK_6: ULong = RANK_1 shl 40
    val RANK_7: ULong = RANK_1 shl 48
    val RANK_8: ULong = RANK_1 shl 56

    // --- Single-step shifts (edge-safe) -----------------------------------

    fun north(b: ULong): ULong = b shl 8
    fun south(b: ULong): ULong = b shr 8
    fun east(b: ULong): ULong = (b and NOT_FILE_H) shl 1
    fun west(b: ULong): ULong = (b and NOT_FILE_A) shr 1
    fun northEast(b: ULong): ULong = (b and NOT_FILE_H) shl 9
    fun northWest(b: ULong): ULong = (b and NOT_FILE_A) shl 7
    fun southEast(b: ULong): ULong = (b and NOT_FILE_H) shr 7
    fun southWest(b: ULong): ULong = (b and NOT_FILE_A) shr 9

    // --- Precomputed attack tables ----------------------------------------

    val KNIGHT_ATTACKS: ULongArray = ULongArray(64) { sq -> knightAttacksFrom(sq) }
    val KING_ATTACKS: ULongArray = ULongArray(64) { sq -> kingAttacksFrom(sq) }
    /** PAWN_ATTACKS[colorOrdinal][square] — attacked squares (NOT push targets). */
    val PAWN_ATTACKS: Array<ULongArray> = arrayOf(
        ULongArray(64) { sq -> whitePawnAttacksFrom(sq) },
        ULongArray(64) { sq -> blackPawnAttacksFrom(sq) },
    )

    private fun knightAttacksFrom(sq: Int): ULong {
        val b = 1uL shl sq
        var attacks = 0uL
        attacks = attacks or ((b and NOT_FILE_H) shl 17)   // NNE
        attacks = attacks or ((b and NOT_FILE_A) shl 15)   // NNW
        attacks = attacks or ((b and NOT_FILE_GH) shl 10)  // NEE
        attacks = attacks or ((b and NOT_FILE_AB) shl 6)   // NWW
        attacks = attacks or ((b and NOT_FILE_H) shr 15)   // SSE  (south-south-east: sq - 15)
        attacks = attacks or ((b and NOT_FILE_A) shr 17)   // SSW
        attacks = attacks or ((b and NOT_FILE_GH) shr 6)   // SEE
        attacks = attacks or ((b and NOT_FILE_AB) shr 10)  // SWW
        return attacks
    }

    private fun kingAttacksFrom(sq: Int): ULong {
        val b = 1uL shl sq
        var attacks = 0uL
        attacks = attacks or north(b)
        attacks = attacks or south(b)
        attacks = attacks or east(b)
        attacks = attacks or west(b)
        attacks = attacks or northEast(b)
        attacks = attacks or northWest(b)
        attacks = attacks or southEast(b)
        attacks = attacks or southWest(b)
        return attacks
    }

    private fun whitePawnAttacksFrom(sq: Int): ULong {
        val b = 1uL shl sq
        return northEast(b) or northWest(b)
    }

    private fun blackPawnAttacksFrom(sq: Int): ULong {
        val b = 1uL shl sq
        return southEast(b) or southWest(b)
    }

    // --- Ray-based slider attacks -----------------------------------------

    /** Direction deltas as (rank, file) increments. */
    private val ROOK_DIRS = arrayOf(
        intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1),
    )
    private val BISHOP_DIRS = arrayOf(
        intArrayOf(1, 1), intArrayOf(1, -1), intArrayOf(-1, 1), intArrayOf(-1, -1),
    )

    fun rookAttacks(sq: Int, occupancy: ULong): ULong = slidingAttacks(sq, occupancy, ROOK_DIRS)
    fun bishopAttacks(sq: Int, occupancy: ULong): ULong = slidingAttacks(sq, occupancy, BISHOP_DIRS)
    fun queenAttacks(sq: Int, occupancy: ULong): ULong =
        rookAttacks(sq, occupancy) or bishopAttacks(sq, occupancy)

    private fun slidingAttacks(sq: Int, occupancy: ULong, dirs: Array<IntArray>): ULong {
        var attacks = 0uL
        val startRank = sq ushr 3
        val startFile = sq and 7
        for (d in dirs) {
            var r = startRank + d[0]
            var f = startFile + d[1]
            while (r in 0..7 && f in 0..7) {
                val target = r * 8 + f
                val targetBb = 1uL shl target
                attacks = attacks or targetBb
                if ((occupancy and targetBb) != 0uL) break // blocker — include it (capture)
                r += d[0]
                f += d[1]
            }
        }
        return attacks
    }

    // --- Bit iteration helpers --------------------------------------------

    /** Index of least-significant set bit. Caller must ensure b != 0. */
    inline fun lsb(b: ULong): Int = b.countTrailingZeroBits()

    /** Returns b with its lowest set bit cleared. */
    inline fun clearLsb(b: ULong): ULong = b and (b - 1uL)

    inline fun popcount(b: ULong): Int = b.countOneBits()
}

/**
 * Iterate over each set bit of [bb], invoking [block] with the bit's square index.
 * Inline + local var to avoid allocation per call (perft hot path).
 */
internal inline fun ULong.forEachSquare(block: (Int) -> Unit) {
    var b = this
    while (b != 0uL) {
        val sq = b.countTrailingZeroBits()
        block(sq)
        b = b and (b - 1uL)
    }
}
