package checkmate.util

import checkmate.model.CastlingRights
import checkmate.model.GameState
import checkmate.model.Player
import checkmate.model.Type
import checkmate.moves.model.*
import kotlin.math.abs


internal fun GameState.toBitmapGameState(): BitmapGameState {
    val bitmapGameState = BitmapGameState()

    for (rank in board.indices) {
        for (file in board[rank].indices) {
            val piece = board[rank][file]
            if (piece != null) {
                val bitPosition = 1UL shl (rank * 8 + file)
                when (piece.type) {
                    Type.PAWN -> if (piece.color == Player.WHITE) bitmapGameState.whitePawns = bitmapGameState.whitePawns or bitPosition else bitmapGameState.blackPawns = bitmapGameState.blackPawns or bitPosition
                    Type.KNIGHT -> if (piece.color == Player.WHITE) bitmapGameState.whiteKnights = bitmapGameState.whiteKnights or bitPosition else bitmapGameState.blackKnights = bitmapGameState.blackKnights or bitPosition
                    Type.BISHOP -> if (piece.color == Player.WHITE) bitmapGameState.whiteBishops = bitmapGameState.whiteBishops or bitPosition else bitmapGameState.blackBishops = bitmapGameState.blackBishops or bitPosition
                    Type.ROOK -> if (piece.color == Player.WHITE) bitmapGameState.whiteRooks = bitmapGameState.whiteRooks or bitPosition else bitmapGameState.blackRooks = bitmapGameState.blackRooks or bitPosition
                    Type.QUEEN -> if (piece.color == Player.WHITE) bitmapGameState.whiteQueens = bitmapGameState.whiteQueens or bitPosition else bitmapGameState.blackQueens = bitmapGameState.blackQueens or bitPosition
                    Type.KING -> if (piece.color == Player.WHITE) bitmapGameState.whiteKing = bitmapGameState.whiteKing or bitPosition else bitmapGameState.blackKing = bitmapGameState.blackKing or bitPosition
                }
            }
        }
    }

    bitmapGameState.isWhiteTurn = currentPlayer == Player.WHITE
    bitmapGameState.castlingRights = castlingRights.calculateCastlingRights()
    bitmapGameState.enPassantTarget = calculateEnPassantTarget()
    bitmapGameState.halfmoveClock = halfMoveClock
    bitmapGameState.fullmoveNumber = fullMoveNumber

    bitmapGameState.updateAllPieces()
    return bitmapGameState
}

private fun CastlingRights.calculateCastlingRights(): Int {
    var castlingRights = 0
    if (whiteKingSide) castlingRights = castlingRights or WHITE_KING_SIDE_CASTLING
    if (whiteQueenSide) castlingRights = castlingRights or WHITE_QUEEN_SIDE_CASTLING
    if (blackKingSide) castlingRights = castlingRights or BLACK_KING_SIDE_CASTLING
    if (blackQueenSide) castlingRights = castlingRights or BLACK_QUEEN_SIDE_CASTLING
    return castlingRights
}

private fun GameState.calculateEnPassantTarget(): ULong {
    if (lastMove == null) return 0UL

    val fromPiece = board[lastMove.to.rank][lastMove.to.file]
    if (fromPiece?.type == Type.PAWN && abs(lastMove.from.rank - lastMove.to.rank) == 2) {
        val enPassantRank = lastMove.from.rank + ((lastMove.to.rank - lastMove.from.rank) / 2)
        val enPassantFile = lastMove.to.file
        return 1UL shl (enPassantRank * 8 + enPassantFile)
    }

    return 0UL
}