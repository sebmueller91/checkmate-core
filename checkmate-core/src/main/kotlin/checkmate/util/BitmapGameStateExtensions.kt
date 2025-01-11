import checkmate.model.*
import checkmate.moves.isCheckmate
import checkmate.moves.model.*

internal fun BitmapGameState.toGameState(lastMove: Move?): GameState {
    val board = List(8) { MutableList<Piece?>(8) { null } }

    fun setPieces(bitboard: ULong, type: Type, color: Player) {
        for (i in 0 until 64) {
            if ((bitboard shr i) and 1UL == 1UL) {
                val rank = i / 8
                val file = i % 8
                board[rank][file] = Piece(type, color)
            }
        }
    }

    setPieces(whitePawns, Type.PAWN, Player.WHITE)
    setPieces(blackPawns, Type.PAWN, Player.BLACK)
    setPieces(whiteKnights, Type.KNIGHT, Player.WHITE)
    setPieces(blackKnights, Type.KNIGHT, Player.BLACK)
    setPieces(whiteBishops, Type.BISHOP, Player.WHITE)
    setPieces(blackBishops, Type.BISHOP, Player.BLACK)
    setPieces(whiteRooks, Type.ROOK, Player.WHITE)
    setPieces(blackRooks, Type.ROOK, Player.BLACK)
    setPieces(whiteQueens, Type.QUEEN, Player.WHITE)
    setPieces(blackQueens, Type.QUEEN, Player.BLACK)
    setPieces(whiteKing, Type.KING, Player.WHITE)
    setPieces(blackKing, Type.KING, Player.BLACK)

    val currentPlayer = if (isWhiteTurn) Player.WHITE else Player.BLACK

    // TODO: Test
//    val gameStatus = getGameStatus()
    val gameStatus = GameStatus.ONGOING

    val castlingRights = CastlingRights(
        blackKingSide = (castlingRights and BLACK_KING_SIDE_CASTLING) != 0,
        blackQueenSide = (castlingRights and BLACK_QUEEN_SIDE_CASTLING) != 0,
        whiteKingSide = (castlingRights and WHITE_KING_SIDE_CASTLING) != 0,
        whiteQueenSide = (castlingRights and WHITE_QUEEN_SIDE_CASTLING) != 0,
    )

    return GameState(
        board = board,
        currentPlayer = currentPlayer,
        gameStatus = gameStatus,
        castlingRights = castlingRights,
        halfMoveClock = halfmoveClock,
        fullMoveNumber = fullmoveNumber,
        lastMove = lastMove
    )
}

private fun BitmapGameState.getGameStatus() =
    if (halfmoveClock >= 100) {
        GameStatus.DRAW
    } else if (isCheckmate(this)) {
        if (isWhiteTurn) GameStatus.CHECKMATE_WHITE else GameStatus.CHECKMATE_BLACK
    } else {
        GameStatus.ONGOING
    }