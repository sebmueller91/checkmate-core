package dev.smueller.checkmate

import dev.smueller.checkmate.Bitboards.bishopAttacks
import dev.smueller.checkmate.Bitboards.queenAttacks
import dev.smueller.checkmate.Bitboards.rookAttacks
import dev.smueller.checkmate.notation.Fen

/**
 * Immutable chess position. All state needed to fully describe a position
 * (FEN-equivalent) plus a cached Zobrist hash and per-color occupancies.
 *
 * Use [Position.initial] for the starting position; [makeMove] returns a new
 * Position with the move applied (does NOT verify legality — the caller must
 * pass a move from [legalMoves]).
 */
class Position internal constructor(
    internal val pieces: ULongArray,           // [12], indexed by Piece.ordinal
    val sideToMove: Color,
    val castlingRights: CastlingRights,
    val enPassantSquare: Square?,
    val halfmoveClock: Int,
    val fullmoveNumber: Int,
    val zobristHash: ULong,
) {
    init {
        require(pieces.size == 12) { "pieces array must have size 12" }
    }

    val whiteOccupancy: ULong = run {
        var b = 0uL
        for (i in 0..5) b = b or pieces[i]
        b
    }
    val blackOccupancy: ULong = run {
        var b = 0uL
        for (i in 6..11) b = b or pieces[i]
        b
    }
    val occupancy: ULong get() = whiteOccupancy or blackOccupancy

    fun bitboard(piece: Piece): ULong = pieces[piece.ordinal]

    fun pieceAt(square: Square): Piece? {
        val mask = square.bitboard
        for (p in Piece.entries) if (pieces[p.ordinal] and mask != 0uL) return p
        return null
    }

    fun occupancyOf(color: Color): ULong =
        if (color == Color.WHITE) whiteOccupancy else blackOccupancy

    fun kingSquare(color: Color): Square {
        val bb = pieces[Piece.of(color, PieceType.KING).ordinal]
        check(bb != 0uL) { "No $color king on board" }
        return Square(Bitboards.lsb(bb))
    }

    /** True iff [square] is attacked by any piece of color [by]. */
    fun isAttacked(square: Int, by: Color): Boolean {
        val occ = occupancy
        // Pawns: a pawn of color `by` attacks `square` iff the opposite-color
        // pawn at `square` would attack the pawn's square. Use the inverse
        // attacker color's pawn-attack table from the target square.
        val pawnAttackers = Bitboards.PAWN_ATTACKS[by.opposite().ordinal][square]
        if (pawnAttackers and pieces[Piece.of(by, PieceType.PAWN).ordinal] != 0uL) return true

        if (Bitboards.KNIGHT_ATTACKS[square] and pieces[Piece.of(by, PieceType.KNIGHT).ordinal] != 0uL) return true
        if (Bitboards.KING_ATTACKS[square] and pieces[Piece.of(by, PieceType.KING).ordinal] != 0uL) return true

        val bishopsQueens =
            pieces[Piece.of(by, PieceType.BISHOP).ordinal] or pieces[Piece.of(by, PieceType.QUEEN).ordinal]
        if (bishopAttacks(square, occ) and bishopsQueens != 0uL) return true

        val rooksQueens =
            pieces[Piece.of(by, PieceType.ROOK).ordinal] or pieces[Piece.of(by, PieceType.QUEEN).ordinal]
        if (rookAttacks(square, occ) and rooksQueens != 0uL) return true

        return false
    }

    fun isInCheck(color: Color = sideToMove): Boolean =
        isAttacked(kingSquare(color).index, color.opposite())

    fun legalMoves(): List<Move> = MoveGen.legalMoves(this)

    fun makeMove(move: Move): Position = makeMoveInternal(this, move)

    /**
     * Game status detection (does NOT include threefold repetition — that requires
     * position history; pass via [statusWithHistory]).
     */
    fun status(): GameStatus = computeStatus(emptyList())

    fun statusWithHistory(positionHashHistory: List<ULong>): GameStatus =
        computeStatus(positionHashHistory)

    private fun computeStatus(history: List<ULong>): GameStatus {
        if (legalMoves().isEmpty()) {
            return if (isInCheck()) GameStatus.Checkmate(sideToMove.opposite())
            else GameStatus.Draw(DrawReason.STALEMATE)
        }
        if (halfmoveClock >= 100) return GameStatus.Draw(DrawReason.FIFTY_MOVE_RULE)
        if (isInsufficientMaterial()) return GameStatus.Draw(DrawReason.INSUFFICIENT_MATERIAL)
        if (history.isNotEmpty()) {
            val occurrences = history.count { it == zobristHash } + 1 // +1 for current
            if (occurrences >= 3) return GameStatus.Draw(DrawReason.THREEFOLD_REPETITION)
        }
        return GameStatus.Ongoing
    }

    private fun isInsufficientMaterial(): Boolean {
        // Any pawn, rook, or queen on board → not insufficient.
        val majors = pieces[Piece.WHITE_PAWN.ordinal] or pieces[Piece.BLACK_PAWN.ordinal] or
            pieces[Piece.WHITE_ROOK.ordinal] or pieces[Piece.BLACK_ROOK.ordinal] or
            pieces[Piece.WHITE_QUEEN.ordinal] or pieces[Piece.BLACK_QUEEN.ordinal]
        if (majors != 0uL) return false

        val whiteMinors = Bitboards.popcount(
            pieces[Piece.WHITE_KNIGHT.ordinal] or pieces[Piece.WHITE_BISHOP.ordinal]
        )
        val blackMinors = Bitboards.popcount(
            pieces[Piece.BLACK_KNIGHT.ordinal] or pieces[Piece.BLACK_BISHOP.ordinal]
        )

        // K vs K
        if (whiteMinors == 0 && blackMinors == 0) return true
        // K+minor vs K (single knight or single bishop)
        if ((whiteMinors == 1 && blackMinors == 0) || (whiteMinors == 0 && blackMinors == 1)) return true
        // K+B vs K+B with bishops on same color squares
        if (whiteMinors == 1 && blackMinors == 1 &&
            pieces[Piece.WHITE_BISHOP.ordinal] != 0uL &&
            pieces[Piece.BLACK_BISHOP.ordinal] != 0uL
        ) {
            val whiteBishopSq = Bitboards.lsb(pieces[Piece.WHITE_BISHOP.ordinal])
            val blackBishopSq = Bitboards.lsb(pieces[Piece.BLACK_BISHOP.ordinal])
            if (squareColor(whiteBishopSq) == squareColor(blackBishopSq)) return true
        }
        return false
    }

    private fun squareColor(sq: Int): Int = ((sq ushr 3) + (sq and 7)) and 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Position) return false
        if (sideToMove != other.sideToMove) return false
        if (castlingRights != other.castlingRights) return false
        if (enPassantSquare != other.enPassantSquare) return false
        if (halfmoveClock != other.halfmoveClock) return false
        if (fullmoveNumber != other.fullmoveNumber) return false
        for (i in 0..11) if (pieces[i] != other.pieces[i]) return false
        return true
    }

    override fun hashCode(): Int = zobristHash.toLong().hashCode()

    fun toFen(): String = Fen.format(this)

    override fun toString(): String = "Position(${toFen()})"

    companion object {
        val initial: Position by lazy {
            Fen.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        }

        fun fromFen(fen: String): Position = Fen.parse(fen)
    }
}
