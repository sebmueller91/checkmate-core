
import checkmate.model.*
import checkmate.moves.isKingInCheck
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

    // TODO: Analyze game status
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

internal fun BitmapGameState.isLegalMove(move: Move): Boolean {
    val newGameState = executeMove(move)
    return !isKingInCheck(newGameState, if (isWhiteTurn) Player.WHITE else Player.BLACK)
}

internal fun BitmapGameState.executeMove(move: Move): BitmapGameState =
    // TODO: Update castling rights

    when (isWhiteTurn) {
        true -> executeWhiteMove(move)
        false -> executeBlackMove(move)
    }

private fun BitmapGameState.executeWhiteMove(move: Move): BitmapGameState {
    val newGameState = this.copy()
    newGameState.isWhiteTurn = !isWhiteTurn
    newGameState.halfmoveClock++
    if (!isWhiteTurn) {
        newGameState.fullmoveNumber++
    }
    move.capture?.let { capturePos ->
        newGameState.whitePawns = whitePawns and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteKnights = whiteKnights and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteBishops = whiteBishops and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteRooks = whiteRooks and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteQueens = whiteQueens and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
    }

    if (whitePawns and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.whitePawns = whitePawns and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whitePawns = whitePawns or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (whiteKnights and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.whiteKnights = whiteKnights and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteKnights = whiteKnights or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (whiteBishops and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.whiteBishops = whiteBishops and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteBishops = whiteBishops or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (whiteRooks and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.whiteRooks = whiteRooks and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteRooks = whiteRooks or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (whiteQueens and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.whiteQueens = whiteQueens and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.whiteQueens = whiteQueens or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    newGameState.updateAllPieces()
    return newGameState
}

private fun BitmapGameState.executeBlackMove(move: Move): BitmapGameState {
    val newGameState = this.copy()
    move.capture?.let { capturePos ->
        newGameState.blackPawns = blackPawns and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackKnights = blackKnights and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackBishops = blackBishops and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackRooks = blackRooks and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackQueens = blackQueens and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
    }

    if (blackPawns and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.blackPawns = blackPawns and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackPawns = blackPawns or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (blackKnights and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.blackKnights = blackKnights and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackKnights = blackKnights or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (blackBishops and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.blackBishops = blackBishops and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackBishops = blackBishops or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (blackRooks and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.blackRooks = blackRooks and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackRooks = blackRooks or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    if (blackQueens and (1UL shl (move.from.rank * 8 + move.from.file)) != 0UL) {
        newGameState.blackQueens = blackQueens and (1UL shl (move.from.rank * 8 + move.from.file)).inv()
        newGameState.blackQueens = blackQueens or (1UL shl (move.to.rank * 8 + move.to.file))
    }
    // TODO: Execute second move if castling
    newGameState.updateAllPieces()
    return newGameState
}