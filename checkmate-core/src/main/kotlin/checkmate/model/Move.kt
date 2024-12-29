package checkmate.model

data class Move(
    val from: Position,
    val to: Position,
    val capture: Position? = null,
    val promotion: Type? = null,
)
