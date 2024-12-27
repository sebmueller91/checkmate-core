package checkmate.moves.model

internal class BitmapGameState {
    var whitePawns: ULong = 0UL
    var whiteKnights: ULong = 0UL
    var whiteBishops: ULong = 0UL
    var whiteRooks: ULong = 0UL
    var whiteQueens: ULong = 0UL
    var whiteKing: ULong = 0UL

    var blackPawns: ULong = 0UL
    var blackKnights: ULong = 0UL
    var blackBishops: ULong = 0UL
    var blackRooks: ULong = 0UL
    var blackQueens: ULong = 0UL
    var blackKing: ULong = 0UL

    var allPieces: ULong = 0UL
    var isWhiteTurn: Boolean = true

    var castlingRights: Int = BLACK_QUEEN_SIDE_CASTLING or BLACK_KING_SIDE_CASTLING or WHITE_KING_SIDE_CASTLING or WHITE_QUEEN_SIDE_CASTLING
    var enPassantTarget: ULong = 0UL
    var halfmoveClock: Int = 0
    var fullmoveNumber: Int = 1
    
    fun updateAllPieces() {
        allPieces = whitePawns or whiteKnights or whiteBishops or whiteRooks or whiteQueens or whiteKing or
                blackPawns or blackKnights or blackBishops or blackRooks or blackQueens or blackKing
    }

    fun initializeStartingPosition() {
        blackPawns = 0x000000000000FF00UL
        blackKnights = 0x0000000000000042UL
        blackBishops = 0x0000000000000024UL
        blackRooks = 0x0000000000000081UL
        blackQueens = 0x0000000000000008UL
        blackKing = 0x0000000000000010UL

        whitePawns = 0x00FF000000000000UL
        whiteKnights = 0x4200000000000000UL
        whiteBishops = 0x2400000000000000UL
        whiteRooks = 0x8100000000000000UL
        whiteQueens = 0x0800000000000000UL
        whiteKing = 0x1000000000000000UL

        updateAllPieces()

        isWhiteTurn = true
        castlingRights = 0b1111
        enPassantTarget = 0UL
        halfmoveClock = 0
        fullmoveNumber = 1
    }
}