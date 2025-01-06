package checkmate.moves

import checkmate.model.Move
import checkmate.moves.model.*

internal fun BitmapGameState.executeMove(move: Move): BitmapGameState {
    val newGameState = when (isWhiteTurn) {
        true -> executeWhiteMove(move)
        false -> executeBlackMove(move)
    }
    newGameState.updateCastingRights(move)
    return newGameState
}

private fun BitmapGameState.updateCastingRights(move: Move) {
    if (move.from == WHITE_ROOK_LEFT_INITIAL_POS || move.from == WHITE_KING_INITIAL_POS) {
        castlingRights = castlingRights and WHITE_QUEEN_SIDE_CASTLING.inv()
    }
    if (move.from == WHITE_ROOK_RIGHT_INITIAL_POS || move.from == WHITE_KING_INITIAL_POS) {
        castlingRights = castlingRights and WHITE_KING_SIDE_CASTLING.inv()
    }
    if (move.from == BLACK_ROOK_LEFT_INITIAL_POS || move.from == BLACK_KING_INITIAL_POS) {
        castlingRights = castlingRights and BLACK_QUEEN_SIDE_CASTLING.inv()
    }
    if (move.from == BLACK_ROOK_RIGHT_INITIAL_POS || move.from == BLACK_KING_INITIAL_POS) {
        castlingRights = castlingRights and BLACK_KING_SIDE_CASTLING.inv()
    }
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
    // TODO: Update castling rook
    // TODO: Perform promotion
    // TODO: Evaluate game state
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