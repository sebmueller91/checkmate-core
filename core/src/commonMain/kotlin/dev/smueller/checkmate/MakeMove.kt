package dev.smueller.checkmate

import dev.smueller.checkmate.Zobrist.BLACK_TO_MOVE
import dev.smueller.checkmate.Zobrist.CASTLING_KEYS
import dev.smueller.checkmate.Zobrist.EN_PASSANT_FILE

/**
 * Apply [move] to [pos] and return the resulting Position. Does NOT verify
 * that the move is legal — call [Position.legalMoves] first and pass one of
 * those moves.
 */
internal fun makeMoveInternal(pos: Position, move: Move): Position {
    val pieces = pos.pieces.copyOf()
    val us = pos.sideToMove
    val them = us.opposite()
    val from = move.from.index
    val to = move.to.index
    val fromBb = 1uL shl from
    val toBb = 1uL shl to

    val moverPiece = piecePieceAt(pieces, from) ?: error("No piece on $from for move $move")
    val moverIdx = moverPiece.ordinal

    var hash = pos.zobristHash

    // Remove EP file from hash (will re-add new EP later if applicable).
    pos.enPassantSquare?.let { hash = hash xor EN_PASSANT_FILE[it.file] }

    // Remove old castling-rights key.
    hash = hash xor CASTLING_KEYS[pos.castlingRights.mask]

    var newCastling = pos.castlingRights
    var newEnPassant: Square? = null
    var halfmoveClock = pos.halfmoveClock + 1

    when (move.kind) {
        MoveKind.QUIET -> {
            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv() or toBb
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            hash = hash xor Zobrist.pieceKey(moverPiece, to)
            if (moverPiece.type == PieceType.PAWN) halfmoveClock = 0
        }
        MoveKind.DOUBLE_PUSH -> {
            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv() or toBb
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            hash = hash xor Zobrist.pieceKey(moverPiece, to)
            halfmoveClock = 0
            // En-passant target is the square the pawn jumped *over*.
            val epIdx = if (us == Color.WHITE) to - 8 else to + 8
            newEnPassant = Square(epIdx)
        }
        MoveKind.CAPTURE -> {
            val capturedPiece = piecePieceAt(pieces, to)
                ?: error("Capture to empty square: $move")
            pieces[capturedPiece.ordinal] = pieces[capturedPiece.ordinal] and toBb.inv()
            hash = hash xor Zobrist.pieceKey(capturedPiece, to)

            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv() or toBb
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            hash = hash xor Zobrist.pieceKey(moverPiece, to)
            halfmoveClock = 0
        }
        MoveKind.EN_PASSANT -> {
            // Captured pawn is one rank behind the target square (relative to mover).
            val capturedSq = if (us == Color.WHITE) to - 8 else to + 8
            val capturedPawn = Piece.of(them, PieceType.PAWN)
            pieces[capturedPawn.ordinal] = pieces[capturedPawn.ordinal] and (1uL shl capturedSq).inv()
            hash = hash xor Zobrist.pieceKey(capturedPawn, capturedSq)

            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv() or toBb
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            hash = hash xor Zobrist.pieceKey(moverPiece, to)
            halfmoveClock = 0
        }
        MoveKind.CASTLE_KING, MoveKind.CASTLE_QUEEN -> {
            // Move king
            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv() or toBb
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            hash = hash xor Zobrist.pieceKey(moverPiece, to)

            // Move rook
            val rookPiece = Piece.of(us, PieceType.ROOK)
            val (rookFrom, rookTo) = if (move.kind == MoveKind.CASTLE_KING) {
                if (us == Color.WHITE) Pair(Square.H1.index, Square.F1.index)
                else Pair(Square.H8.index, Square.F8.index)
            } else {
                if (us == Color.WHITE) Pair(Square.A1.index, Square.D1.index)
                else Pair(Square.A8.index, Square.D8.index)
            }
            val rookFromBb = 1uL shl rookFrom
            val rookToBb = 1uL shl rookTo
            pieces[rookPiece.ordinal] = pieces[rookPiece.ordinal] and rookFromBb.inv() or rookToBb
            hash = hash xor Zobrist.pieceKey(rookPiece, rookFrom)
            hash = hash xor Zobrist.pieceKey(rookPiece, rookTo)
        }
        MoveKind.PROMO_KNIGHT, MoveKind.PROMO_BISHOP, MoveKind.PROMO_ROOK, MoveKind.PROMO_QUEEN -> {
            val promoPiece = Piece.of(us, move.promotionPiece!!)
            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv()
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            pieces[promoPiece.ordinal] = pieces[promoPiece.ordinal] or toBb
            hash = hash xor Zobrist.pieceKey(promoPiece, to)
            halfmoveClock = 0
        }
        MoveKind.PROMO_KNIGHT_CAPTURE, MoveKind.PROMO_BISHOP_CAPTURE,
        MoveKind.PROMO_ROOK_CAPTURE, MoveKind.PROMO_QUEEN_CAPTURE -> {
            val capturedPiece = piecePieceAt(pieces, to)
                ?: error("Promo-capture to empty square: $move")
            pieces[capturedPiece.ordinal] = pieces[capturedPiece.ordinal] and toBb.inv()
            hash = hash xor Zobrist.pieceKey(capturedPiece, to)

            val promoPiece = Piece.of(us, move.promotionPiece!!)
            pieces[moverIdx] = pieces[moverIdx] and fromBb.inv()
            hash = hash xor Zobrist.pieceKey(moverPiece, from)
            pieces[promoPiece.ordinal] = pieces[promoPiece.ordinal] or toBb
            hash = hash xor Zobrist.pieceKey(promoPiece, to)
            halfmoveClock = 0
        }
    }

    // Update castling rights — lose them if the king or relevant rook moved or
    // was captured. Operate on the original mask through a single combined mask.
    val rightsLossMask = castlingRightsLossMask(from, to, moverPiece)
    if (rightsLossMask != 0) {
        newCastling = newCastling.without(rightsLossMask)
    }
    hash = hash xor CASTLING_KEYS[newCastling.mask]

    // Add new EP file to hash if any.
    newEnPassant?.let { hash = hash xor EN_PASSANT_FILE[it.file] }

    // Toggle side.
    hash = hash xor BLACK_TO_MOVE

    return Position(
        pieces = pieces,
        sideToMove = them,
        castlingRights = newCastling,
        enPassantSquare = newEnPassant,
        halfmoveClock = halfmoveClock,
        fullmoveNumber = pos.fullmoveNumber + (if (us == Color.BLACK) 1 else 0),
        zobristHash = hash,
    )
}

/**
 * Castling-rights mask to clear given a piece moving from `from` to `to`:
 * king move clears both, rook move from corner clears that corner's bit,
 * rook capture on corner (i.e., the destination is a corner with the enemy
 * rook starting there) also clears that corner's bit.
 */
private fun castlingRightsLossMask(from: Int, to: Int, mover: Piece): Int {
    var mask = 0
    if (mover == Piece.WHITE_KING) mask = mask or CastlingRights.WHITE_BOTH
    if (mover == Piece.BLACK_KING) mask = mask or CastlingRights.BLACK_BOTH
    // Rook leaves its starting corner — lose that side.
    when (from) {
        Square.A1.index -> mask = mask or CastlingRights.WQ
        Square.H1.index -> mask = mask or CastlingRights.WK
        Square.A8.index -> mask = mask or CastlingRights.BQ
        Square.H8.index -> mask = mask or CastlingRights.BK
    }
    // Anything captured on a corner where a rook should be — lose that side.
    when (to) {
        Square.A1.index -> mask = mask or CastlingRights.WQ
        Square.H1.index -> mask = mask or CastlingRights.WK
        Square.A8.index -> mask = mask or CastlingRights.BQ
        Square.H8.index -> mask = mask or CastlingRights.BK
    }
    return mask
}

/** Find the piece at `square` in the given pieces array, or null. */
private fun piecePieceAt(pieces: ULongArray, square: Int): Piece? {
    val mask = 1uL shl square
    for (p in Piece.entries) if (pieces[p.ordinal] and mask != 0uL) return p
    return null
}
