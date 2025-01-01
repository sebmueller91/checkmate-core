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

    var whitePieces: ULong = 0UL
    var blackPieces: ULong = 0UL
    var allPieces: ULong = 0UL

    var isWhiteTurn: Boolean = true

    var castlingRights: Int = BLACK_QUEEN_SIDE_CASTLING or BLACK_KING_SIDE_CASTLING or WHITE_KING_SIDE_CASTLING or WHITE_QUEEN_SIDE_CASTLING
    var enPassantTarget: ULong = 0UL
    var halfmoveClock: Int = 0
    var fullmoveNumber: Int = 1

    fun updateAllPieces() {
        whitePieces = whitePawns or whiteKnights or whiteBishops or whiteRooks or whiteQueens or whiteKing
        blackPieces = blackPawns or blackKnights or blackBishops or blackRooks or blackQueens or blackKing
        allPieces = whitePieces or blackPieces
    }

    fun initializeStartingPosition() {
        blackPawns = RANK_7
        blackKnights = (RANK_8 and FILE_B) or (RANK_8 and FILE_G)
        blackBishops = (RANK_8 and FILE_C) or (RANK_8 and FILE_F)
        blackRooks = (RANK_8 and FILE_A) or (RANK_8 and FILE_H)
        blackQueens = RANK_8 and FILE_D
        blackKing = RANK_8 and FILE_E

        whitePawns = RANK_2
        whiteKnights = (RANK_1 and FILE_B) or (RANK_1 and FILE_G)
        whiteBishops = (RANK_1 and FILE_C) or (RANK_1 and FILE_F)
        whiteRooks = (RANK_1 and FILE_A) or (RANK_1 and FILE_H)
        whiteQueens = RANK_1 and FILE_D
        whiteKing = RANK_1 and FILE_E

        updateAllPieces()

        isWhiteTurn = true
        castlingRights = 0b1111
        enPassantTarget = 0UL
        halfmoveClock = 0
        fullmoveNumber = 1
    }

    init {
        updateAllPieces()
    }
}