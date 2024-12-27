package checkmate.model

data class GameState(
    val board: List<List<Piece?>>,
    val currentPlayer: Player,
    val gameStatus: GameStatus,
    val castlingRights: CastlingRights,
    val halfMoveClock: Int,
    val fullMoveNumber: Int,
    val lastMove: Move?
)