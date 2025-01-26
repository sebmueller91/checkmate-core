package checkmate.moves.model

import checkmate.model.Position

internal const val FILE_A = 0x0101010101010101UL
internal const val FILE_B = 0x0202020202020202UL
internal const val FILE_C = 0x0404040404040404UL
internal const val FILE_D = 0x0808080808080808UL
internal const val FILE_E = 0x1010101010101010UL
internal const val FILE_F = 0x2020202020202020UL
internal const val FILE_G = 0x4040404040404040UL
internal const val FILE_H = 0x8080808080808080UL

internal const val RANK_1 = 0x00000000000000FFUL
internal const val RANK_2 = 0x000000000000FF00UL
internal const val RANK_3 = 0x0000000000FF0000UL
internal const val RANK_4 = 0x00000000FF000000UL
internal const val RANK_5 = 0x000000FF00000000UL
internal const val RANK_6 = 0x0000FF0000000000UL
internal const val RANK_7 = 0x00FF000000000000UL
internal const val RANK_8 = 0xFF00000000000000UL

internal const val BLACK_QUEEN_SIDE_CASTLING: Int = 0b0001
internal const val BLACK_KING_SIDE_CASTLING: Int  = 0b0010
internal const val WHITE_QUEEN_SIDE_CASTLING: Int  = 0b0100
internal const val WHITE_KING_SIDE_CASTLING: Int  = 0b1000

internal val RANK_MASKS = arrayOf(
    RANK_1,
    RANK_2,
    RANK_3,
    RANK_4,
    RANK_5,
    RANK_6,
    RANK_7,
    RANK_8,
)

internal val FILE_MASKS = arrayOf(
    FILE_A,
    FILE_B,
    FILE_C,
    FILE_D,
    FILE_E,
    FILE_F,
    FILE_G,
    FILE_H
)

internal val WHITE_ROOK_LEFT_INITIAL_POS = Position(0,0)
internal val WHITE_ROOK_RIGHT_INITIAL_POS = Position(0,7)
internal val BLACK_ROOK_LEFT_INITIAL_POS = Position(7,0)
internal val BLACK_ROOK_RIGHT_INITIAL_POS = Position(7,7)
internal val WHITE_KING_INITIAL_POS = Position(0,4)
internal val BLACK_KING_INITIAL_POS = Position(7,4)
