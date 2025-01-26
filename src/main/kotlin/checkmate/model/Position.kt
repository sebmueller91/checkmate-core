package checkmate.model

/*
Both rank and file refer to indices in the range 0-7
Rank 1=0, Rank 2=1, Rank 3=2, Rank 4=3, Rank 5=4, Rank 6=5, Rank 7=6, Rank 8=7
File A=0, File B=1, File C=2, File D=3, File E=4, File F=5, File G=6, File H=7
 */
data class Position(
    val rank: Int,
    val file: Int
) {
    operator fun plus(p: Position) = Position(rank + p.rank, file + p.file)
    operator fun minus(p: Position) = Position(rank + p.rank, file + p.file)
}