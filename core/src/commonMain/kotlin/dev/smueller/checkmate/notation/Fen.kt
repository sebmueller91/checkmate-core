package dev.smueller.checkmate.notation

import dev.smueller.checkmate.CastlingRights
import dev.smueller.checkmate.Color
import dev.smueller.checkmate.Piece
import dev.smueller.checkmate.Position
import dev.smueller.checkmate.Square
import dev.smueller.checkmate.Zobrist

object Fen {
    /** Parses a FEN string and returns the corresponding [Position]. */
    fun parse(fen: String): Position = parseFen(fen)

    /** Serialize a [Position] back to FEN. */
    fun format(pos: Position): String = formatFen(pos)
}

private fun parseFen(fen: String): Position {
    val parts = fen.trim().split(Regex("\\s+"))
    require(parts.size in 4..6) { "FEN must have 4-6 fields, got ${parts.size}: $fen" }

    val (placement, sideStr, castlingStr, epStr) = parts.let { listOf(it[0], it[1], it[2], it[3]) }
    val halfmove = if (parts.size >= 5) parts[4].toInt() else 0
    val fullmove = if (parts.size >= 6) parts[5].toInt() else 1

    val pieces = ULongArray(12)
    val ranks = placement.split('/')
    require(ranks.size == 8) { "FEN placement must have 8 ranks, got ${ranks.size}: $placement" }
    for ((rankFromTop, rankStr) in ranks.withIndex()) {
        val rank = 7 - rankFromTop
        var file = 0
        for (c in rankStr) {
            if (c.isDigit()) {
                file += c - '0'
            } else {
                val piece = Piece.fromFenChar(c) ?: error("Invalid FEN piece: $c")
                require(file in 0..7) { "Rank overflow in FEN: $rankStr" }
                val sq = rank * 8 + file
                pieces[piece.ordinal] = pieces[piece.ordinal] or (1uL shl sq)
                file++
            }
        }
        require(file == 8) { "Rank $rankStr does not fill 8 files (got $file)" }
    }

    val sideToMove = when (sideStr) {
        "w" -> Color.WHITE
        "b" -> Color.BLACK
        else -> error("Invalid side-to-move: $sideStr")
    }
    val castling = CastlingRights.fromFen(castlingStr)
    val ep = if (epStr == "-") null else Square.parse(epStr)

    val hash = computeZobristHash(pieces, sideToMove, castling, ep)
    return Position(
        pieces = pieces,
        sideToMove = sideToMove,
        castlingRights = castling,
        enPassantSquare = ep,
        halfmoveClock = halfmove,
        fullmoveNumber = fullmove,
        zobristHash = hash,
    )
}

private fun formatFen(pos: Position): String {
    val sb = StringBuilder()

    // Placement, ranks 8 down to 1.
    for (rank in 7 downTo 0) {
        var emptyCount = 0
        for (file in 0..7) {
            val sq = rank * 8 + file
            val piece = pieceAtIndex(pos, sq)
            if (piece == null) {
                emptyCount++
            } else {
                if (emptyCount > 0) {
                    sb.append(emptyCount); emptyCount = 0
                }
                sb.append(piece.fenChar)
            }
        }
        if (emptyCount > 0) sb.append(emptyCount)
        if (rank > 0) sb.append('/')
    }

    sb.append(' ').append(if (pos.sideToMove == Color.WHITE) 'w' else 'b')
    sb.append(' ').append(pos.castlingRights.toFen())
    sb.append(' ').append(pos.enPassantSquare?.notation() ?: "-")
    sb.append(' ').append(pos.halfmoveClock)
    sb.append(' ').append(pos.fullmoveNumber)

    return sb.toString()
}

private fun pieceAtIndex(pos: Position, sq: Int): Piece? {
    val mask = 1uL shl sq
    for (p in Piece.entries) if (pos.bitboard(p) and mask != 0uL) return p
    return null
}

internal fun computeZobristHash(
    pieces: ULongArray,
    sideToMove: Color,
    castling: CastlingRights,
    ep: Square?,
): ULong {
    var h = 0uL
    for (pi in 0..11) {
        var bb = pieces[pi]
        while (bb != 0uL) {
            val sq = bb.countTrailingZeroBits()
            h = h xor Zobrist.PIECE_KEYS[pi][sq]
            bb = bb and (bb - 1uL)
        }
    }
    h = h xor Zobrist.CASTLING_KEYS[castling.mask]
    if (ep != null) h = h xor Zobrist.EN_PASSANT_FILE[ep.file]
    if (sideToMove == Color.BLACK) h = h xor Zobrist.BLACK_TO_MOVE
    return h
}
