package checkmate.model

data class Piece(
    val type: Type,
    val color: Player,
    val isMoved: Boolean
)
