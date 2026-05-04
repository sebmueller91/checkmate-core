package dev.smueller.checkmate

import kotlin.jvm.JvmInline

/**
 * 4-bit castling-rights mask. Internal representation is "rook-square aware":
 *   bit 0 — White can castle with the rook on h-file (kingside)
 *   bit 1 — White can castle with the rook on a-file (queenside)
 *   bit 2 — Black can castle with the rook on h-file (kingside)
 *   bit 3 — Black can castle with the rook on a-file (queenside)
 *
 * For standard chess the rook files are fixed (a/h). For Chess960 the same
 * encoding can be reused by remapping rook files per game; not implemented in v1
 * but the encoding itself is forward-compatible.
 */
@JvmInline
value class CastlingRights(val mask: Int) {
    init {
        require(mask in 0..15) { "CastlingRights mask out of range: $mask" }
    }

    fun canCastleKingside(color: Color): Boolean =
        (mask and (if (color == Color.WHITE) WK else BK)) != 0

    fun canCastleQueenside(color: Color): Boolean =
        (mask and (if (color == Color.WHITE) WQ else BQ)) != 0

    fun without(bits: Int): CastlingRights = CastlingRights(mask and bits.inv() and 0xF)
    fun with(bits: Int): CastlingRights = CastlingRights((mask or bits) and 0xF)

    fun toFen(): String {
        if (mask == 0) return "-"
        val sb = StringBuilder()
        if (mask and WK != 0) sb.append('K')
        if (mask and WQ != 0) sb.append('Q')
        if (mask and BK != 0) sb.append('k')
        if (mask and BQ != 0) sb.append('q')
        return sb.toString()
    }

    companion object {
        const val WK = 1
        const val WQ = 2
        const val BK = 4
        const val BQ = 8
        const val WHITE_BOTH = WK or WQ
        const val BLACK_BOTH = BK or BQ
        const val ALL = WHITE_BOTH or BLACK_BOTH

        val NONE = CastlingRights(0)
        val FULL = CastlingRights(ALL)

        fun fromFen(s: String): CastlingRights {
            if (s == "-") return NONE
            var mask = 0
            for (c in s) when (c) {
                'K' -> mask = mask or WK
                'Q' -> mask = mask or WQ
                'k' -> mask = mask or BK
                'q' -> mask = mask or BQ
                else -> error("Invalid castling rights character: $c")
            }
            return CastlingRights(mask)
        }
    }
}
