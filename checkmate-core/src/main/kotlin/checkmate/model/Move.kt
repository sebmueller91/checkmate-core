package checkmate.model

data class Move(
    val from: Position,
    val to: Position,
    val promotion: Type? = null
)
