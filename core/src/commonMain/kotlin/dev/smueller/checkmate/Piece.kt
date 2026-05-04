package dev.smueller.checkmate

enum class Piece(val color: Color, val type: PieceType) {
    WHITE_PAWN(Color.WHITE, PieceType.PAWN),
    WHITE_KNIGHT(Color.WHITE, PieceType.KNIGHT),
    WHITE_BISHOP(Color.WHITE, PieceType.BISHOP),
    WHITE_ROOK(Color.WHITE, PieceType.ROOK),
    WHITE_QUEEN(Color.WHITE, PieceType.QUEEN),
    WHITE_KING(Color.WHITE, PieceType.KING),
    BLACK_PAWN(Color.BLACK, PieceType.PAWN),
    BLACK_KNIGHT(Color.BLACK, PieceType.KNIGHT),
    BLACK_BISHOP(Color.BLACK, PieceType.BISHOP),
    BLACK_ROOK(Color.BLACK, PieceType.ROOK),
    BLACK_QUEEN(Color.BLACK, PieceType.QUEEN),
    BLACK_KING(Color.BLACK, PieceType.KING);

    val fenChar: Char
        get() = when (this) {
            WHITE_PAWN -> 'P'; WHITE_KNIGHT -> 'N'; WHITE_BISHOP -> 'B'
            WHITE_ROOK -> 'R'; WHITE_QUEEN -> 'Q'; WHITE_KING -> 'K'
            BLACK_PAWN -> 'p'; BLACK_KNIGHT -> 'n'; BLACK_BISHOP -> 'b'
            BLACK_ROOK -> 'r'; BLACK_QUEEN -> 'q'; BLACK_KING -> 'k'
        }

    companion object {
        fun of(color: Color, type: PieceType): Piece = when (color) {
            Color.WHITE -> when (type) {
                PieceType.PAWN -> WHITE_PAWN; PieceType.KNIGHT -> WHITE_KNIGHT
                PieceType.BISHOP -> WHITE_BISHOP; PieceType.ROOK -> WHITE_ROOK
                PieceType.QUEEN -> WHITE_QUEEN; PieceType.KING -> WHITE_KING
            }
            Color.BLACK -> when (type) {
                PieceType.PAWN -> BLACK_PAWN; PieceType.KNIGHT -> BLACK_KNIGHT
                PieceType.BISHOP -> BLACK_BISHOP; PieceType.ROOK -> BLACK_ROOK
                PieceType.QUEEN -> BLACK_QUEEN; PieceType.KING -> BLACK_KING
            }
        }

        fun fromFenChar(c: Char): Piece? = when (c) {
            'P' -> WHITE_PAWN; 'N' -> WHITE_KNIGHT; 'B' -> WHITE_BISHOP
            'R' -> WHITE_ROOK; 'Q' -> WHITE_QUEEN; 'K' -> WHITE_KING
            'p' -> BLACK_PAWN; 'n' -> BLACK_KNIGHT; 'b' -> BLACK_BISHOP
            'r' -> BLACK_ROOK; 'q' -> BLACK_QUEEN; 'k' -> BLACK_KING
            else -> null
        }
    }
}
