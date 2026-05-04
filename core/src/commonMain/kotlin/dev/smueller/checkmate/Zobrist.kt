package dev.smueller.checkmate

/**
 * Deterministic Zobrist keys, seeded by a fixed constant so the same position
 * hashes identically on every platform and every run. No SecureRandom (KMP).
 *
 * xorshift64* — well-distributed, very fast, deterministic.
 */
internal object Zobrist {

    private const val SEED: ULong = 0xC0FFEE_BABE_DEADuL

    val PIECE_KEYS: Array<ULongArray>
    val CASTLING_KEYS: ULongArray
    val EN_PASSANT_FILE: ULongArray
    val BLACK_TO_MOVE: ULong

    init {
        val rng = Xorshift(SEED)
        PIECE_KEYS = Array(12) { ULongArray(64) { rng.next() } }
        CASTLING_KEYS = ULongArray(16) { rng.next() }
        EN_PASSANT_FILE = ULongArray(8) { rng.next() }
        BLACK_TO_MOVE = rng.next()
    }

    fun pieceKey(piece: Piece, square: Int): ULong =
        PIECE_KEYS[piece.ordinal][square]

    private class Xorshift(seed: ULong) {
        private var state: ULong = if (seed == 0uL) 1uL else seed
        fun next(): ULong {
            var x = state
            x = x xor (x shr 12)
            x = x xor (x shl 25)
            x = x xor (x shr 27)
            state = x
            return x * 0x2545F4914F6CDD1DuL
        }
    }
}
