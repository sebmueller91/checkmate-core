package dev.smueller.checkmate

import kotlin.jvm.JvmInline

/**
 * Packed move encoding (16 bits used):
 *   bits 0-5   from square (0..63)
 *   bits 6-11  to square (0..63)
 *   bits 12-15 move kind
 */
@JvmInline
value class Move(val packed: UInt) {

    val from: Square get() = Square((packed and 0x3Fu).toInt())
    val to: Square get() = Square(((packed shr 6) and 0x3Fu).toInt())
    val kind: MoveKind get() = MoveKind.entries[((packed shr 12) and 0xFu).toInt()]

    val isPromotion: Boolean get() = kind.isPromotion
    val isCapture: Boolean get() = kind.isCapture
    val isCastle: Boolean get() = kind == MoveKind.CASTLE_KING || kind == MoveKind.CASTLE_QUEEN
    val isEnPassant: Boolean get() = kind == MoveKind.EN_PASSANT
    val isDoublePush: Boolean get() = kind == MoveKind.DOUBLE_PUSH

    val promotionPiece: PieceType?
        get() = when (kind) {
            MoveKind.PROMO_KNIGHT, MoveKind.PROMO_KNIGHT_CAPTURE -> PieceType.KNIGHT
            MoveKind.PROMO_BISHOP, MoveKind.PROMO_BISHOP_CAPTURE -> PieceType.BISHOP
            MoveKind.PROMO_ROOK,   MoveKind.PROMO_ROOK_CAPTURE   -> PieceType.ROOK
            MoveKind.PROMO_QUEEN,  MoveKind.PROMO_QUEEN_CAPTURE  -> PieceType.QUEEN
            else -> null
        }

    /** UCI long-algebraic notation, e.g. "e2e4", "e7e8q". */
    fun toUci(): String {
        val base = from.notation() + to.notation()
        val promo = when (promotionPiece) {
            PieceType.KNIGHT -> "n"; PieceType.BISHOP -> "b"
            PieceType.ROOK -> "r"; PieceType.QUEEN -> "q"
            else -> ""
        }
        return base + promo
    }

    override fun toString(): String = toUci()

    companion object {
        fun of(from: Square, to: Square, kind: MoveKind = MoveKind.QUIET): Move {
            val packed =
                (from.index.toUInt() and 0x3Fu) or
                ((to.index.toUInt() and 0x3Fu) shl 6) or
                ((kind.ordinal.toUInt() and 0xFu) shl 12)
            return Move(packed)
        }
    }
}

enum class MoveKind {
    QUIET,
    DOUBLE_PUSH,
    CASTLE_KING,
    CASTLE_QUEEN,
    CAPTURE,
    EN_PASSANT,
    PROMO_KNIGHT,
    PROMO_BISHOP,
    PROMO_ROOK,
    PROMO_QUEEN,
    PROMO_KNIGHT_CAPTURE,
    PROMO_BISHOP_CAPTURE,
    PROMO_ROOK_CAPTURE,
    PROMO_QUEEN_CAPTURE;

    val isPromotion: Boolean
        get() = this >= PROMO_KNIGHT
    val isCapture: Boolean
        get() = this == CAPTURE || this == EN_PASSANT || this >= PROMO_KNIGHT_CAPTURE
}
