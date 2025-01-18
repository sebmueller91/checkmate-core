package checkmate.moves

import checkmate.model.Move
import checkmate.model.Position
import checkmate.model.Type
import checkmate.moves.model.*

internal fun BitmapGameState.executeMove(move: Move): BitmapGameState {
    val newGameState = when (isWhiteTurn) {
        true -> executeWhiteMove(move)
        false -> executeBlackMove(move)
    }
    return newGameState
}

private fun BitmapGameState.executeWhiteMove(move: Move): BitmapGameState {
    val newGameState = this.copy()

    newGameState.updateGameStats()
    newGameState.enPassantTarget = 0UL

    newGameState.whiteCaptures(move)
    newGameState.whitePawnMoves(move)
    newGameState.whiteRookMoves(move)
    newGameState.whiteBishopMoves(move)
    newGameState.whiteKnightMoves(move)
    newGameState.whiteQueenMoves(move)

    newGameState.updateCastingRights(move)
    newGameState.updateAllPieces()

    return newGameState
}

private fun BitmapGameState.executeBlackMove(move: Move): BitmapGameState {
    val newGameState = this.copy()

    newGameState.updateGameStats()
    newGameState.enPassantTarget = 0UL

    newGameState.blackCaptures(move)
    newGameState.blackPawnMoves(move)
    newGameState.blackRookMoves(move)
    newGameState.blackBishopMoves(move)
    newGameState.blackKnightMoves(move)
    newGameState.blackQueenMoves(move)

    newGameState.updateCastingRights(move)
    newGameState.updateAllPieces()

    return newGameState
}

private fun BitmapGameState.whiteCaptures(move: Move) = move.capture?.let { capturePos ->
    blackPawns = blackPawns and (1UL shl capturePos.bitboardIndex()).inv()
    blackBishops = blackBishops and (1UL shl capturePos.bitboardIndex()).inv()
    blackKnights = blackKnights and (1UL shl capturePos.bitboardIndex()).inv()
    blackRooks = blackRooks and (1UL shl capturePos.bitboardIndex()).inv()
    blackQueens = blackQueens and (1UL shl capturePos.bitboardIndex()).inv()
}

private fun BitmapGameState.blackCaptures(move: Move) = move.capture?.let { capturePos ->
    whitePawns = whitePawns and (1UL shl capturePos.bitboardIndex()).inv()
    whiteBishops = whiteBishops and (1UL shl capturePos.bitboardIndex()).inv()
    whiteKnights = whiteKnights and (1UL shl capturePos.bitboardIndex()).inv()
    whiteRooks = whiteRooks and (1UL shl capturePos.bitboardIndex()).inv()
    whiteQueens = whiteQueens and (1UL shl capturePos.bitboardIndex()).inv()
}

private fun BitmapGameState.whitePawnMoves(move: Move) {
    if (whitePawns and (1UL shl move.from.bitboardIndex()) != 0UL) {
        whitePawns = whitePawns and (1UL shl move.from.bitboardIndex()).inv()
        if (move.promotion != null) {
            promoteWhitePawn(move)
        } else {
            whitePawns = whitePawns or (1UL shl move.to.bitboardIndex())
        }
        if (move.from.rank == 1 && move.to.rank == 3) {
            enPassantTarget = 1UL shl (move.from.bitboardIndex() + 8)
        }
    }
}

private fun BitmapGameState.blackPawnMoves(move: Move) {
    if (blackPawns and (1UL shl move.from.bitboardIndex()) != 0UL) {
        blackPawns = blackPawns and (1UL shl move.from.bitboardIndex()).inv()
        if (move.promotion != null) {
            promoteBlackPawn(move)
        } else {
            blackPawns = blackPawns or (1UL shl move.to.bitboardIndex())
        }
        if (move.from.rank == 6 && move.to.rank == 4) {
            enPassantTarget = 1UL shl (move.from.bitboardIndex() - 8)
        }
    }
}

private fun BitmapGameState.whiteRookMoves(move: Move) {
    if (whiteRooks and (1UL shl move.from.bitboardIndex()) != 0UL) {
        whiteRooks = whiteRooks and (1UL shl move.from.bitboardIndex()).inv()
        whiteRooks = whiteRooks or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.blackRookMoves(move: Move) {
    if (blackRooks and (1UL shl move.from.bitboardIndex()) != 0UL) {
        blackRooks = blackRooks and (1UL shl move.from.bitboardIndex()).inv()
        blackRooks = blackRooks or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.whiteBishopMoves(move: Move) {
    if (whiteBishops and (1UL shl move.from.bitboardIndex()) != 0UL) {
        whiteBishops = whiteBishops and (1UL shl move.from.bitboardIndex()).inv()
        whiteBishops = whiteBishops or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.blackBishopMoves(move: Move) {
    if (blackBishops and (1UL shl move.from.bitboardIndex()) != 0UL) {
        blackBishops = blackBishops and (1UL shl move.from.bitboardIndex()).inv()
        blackBishops = blackBishops or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.whiteKnightMoves(move: Move) {
    if (whiteKnights and (1UL shl move.from.bitboardIndex()) != 0UL) {
        whiteKnights = whiteKnights and (1UL shl move.from.bitboardIndex()).inv()
        whiteKnights = whiteKnights or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.blackKnightMoves(move: Move) {
    if (blackKnights and (1UL shl move.from.bitboardIndex()) != 0UL) {
        blackKnights = blackKnights and (1UL shl move.from.bitboardIndex()).inv()
        blackKnights = blackKnights or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.whiteQueenMoves(move: Move) {
    if (whiteQueens and (1UL shl move.from.bitboardIndex()) != 0UL) {
        whiteQueens = whiteQueens and (1UL shl move.from.bitboardIndex()).inv()
        whiteQueens = whiteQueens or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.blackQueenMoves(move: Move) {
    if (blackQueens and (1UL shl move.from.bitboardIndex()) != 0UL) {
        blackQueens = blackQueens and (1UL shl move.from.bitboardIndex()).inv()
        blackQueens = blackQueens or (1UL shl move.to.bitboardIndex())
    }
}

private fun BitmapGameState.promoteWhitePawn(move: Move) =
    when (move.promotion) {
        Type.QUEEN -> whiteQueens = whiteQueens or (1UL shl move.to.bitboardIndex())
        Type.ROOK -> whiteRooks = whiteRooks or (1UL shl move.to.bitboardIndex())
        Type.BISHOP -> whiteBishops = whiteBishops or (1UL shl move.to.bitboardIndex())
        else -> whiteKnights = whiteKnights or (1UL shl move.to.bitboardIndex())
    }

private fun BitmapGameState.promoteBlackPawn(move: Move) =
    when (move.promotion) {
        Type.QUEEN -> blackQueens = blackQueens or (1UL shl move.to.bitboardIndex())
        Type.ROOK -> blackRooks = blackRooks or (1UL shl move.to.bitboardIndex())
        Type.BISHOP -> blackBishops = blackBishops or (1UL shl move.to.bitboardIndex())
        else -> blackKnights = blackKnights or (1UL shl move.to.bitboardIndex())
    }

private fun BitmapGameState.updateGameStats() {
    halfmoveClock++
    if (!isWhiteTurn) fullmoveNumber++
    isWhiteTurn = !isWhiteTurn
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

private fun Position.bitboardIndex(): Int {
    return rank * 8 + file
}