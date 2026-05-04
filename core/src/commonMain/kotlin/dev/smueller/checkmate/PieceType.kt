package dev.smueller.checkmate

enum class PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    companion object {
        val ALL: List<PieceType> = entries.toList()
    }
}
