package checkmate.model

data class Position(
    val rank: Int,
    val file: Int
) {
    operator fun plus(p: Position) = Position(rank + p.rank, file + p.file)
    operator fun minus(p: Position) = Position(rank + p.rank, file + p.file)
}