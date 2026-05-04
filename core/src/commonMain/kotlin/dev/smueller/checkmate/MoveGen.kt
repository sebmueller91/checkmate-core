package dev.smueller.checkmate

import dev.smueller.checkmate.Bitboards.KING_ATTACKS
import dev.smueller.checkmate.Bitboards.KNIGHT_ATTACKS
import dev.smueller.checkmate.Bitboards.PAWN_ATTACKS
import dev.smueller.checkmate.Bitboards.bishopAttacks
import dev.smueller.checkmate.Bitboards.queenAttacks
import dev.smueller.checkmate.Bitboards.rookAttacks

internal object MoveGen {

    fun legalMoves(pos: Position): List<Move> {
        val pseudo = pseudoLegalMoves(pos)
        val out = ArrayList<Move>(pseudo.size)
        for (m in pseudo) {
            val next = makeMoveInternal(pos, m)
            // After making the move, sideToMove has flipped; the side that just
            // moved is `pos.sideToMove`, and the move was legal iff that side's
            // king is not under attack in the new position.
            if (!next.isAttacked(next.kingSquare(pos.sideToMove).index, pos.sideToMove.opposite())) {
                out.add(m)
            }
        }
        return out
    }

    fun pseudoLegalMoves(pos: Position): List<Move> {
        val moves = ArrayList<Move>(60)
        val us = pos.sideToMove
        val them = us.opposite()
        val ourOcc = pos.occupancyOf(us)
        val theirOcc = pos.occupancyOf(them)
        val occ = pos.occupancy

        generatePawnMoves(pos, moves, us, them, ourOcc, theirOcc, occ)
        generateKnightMoves(pos, moves, us, ourOcc, theirOcc)
        generateSlidingMoves(pos, moves, us, ourOcc, theirOcc, occ)
        generateKingMoves(pos, moves, us, them, ourOcc, theirOcc, occ)
        generateCastlingMoves(pos, moves, us, them, occ)

        return moves
    }

    // --- Pawns -------------------------------------------------------------

    private fun generatePawnMoves(
        pos: Position,
        out: MutableList<Move>,
        us: Color,
        them: Color,
        ourOcc: ULong,
        theirOcc: ULong,
        occ: ULong,
    ) {
        val pawns = pos.bitboard(Piece.of(us, PieceType.PAWN))
        if (pawns == 0uL) return

        val forwardShift = if (us == Color.WHITE) 8 else -8
        val startRank = if (us == Color.WHITE) Bitboards.RANK_2 else Bitboards.RANK_7
        val promoRankFromBelow = if (us == Color.WHITE) Bitboards.RANK_7 else Bitboards.RANK_2

        // Single push (excluding promotions)
        val singlePushTargets = if (us == Color.WHITE)
            (pawns and promoRankFromBelow.inv()) shl 8
        else
            (pawns and promoRankFromBelow.inv()) shr 8
        val singlePushAvailable = singlePushTargets and occ.inv()
        singlePushAvailable.forEachSquare { to ->
            val from = to - forwardShift
            out.add(Move.of(Square(from), Square(to), MoveKind.QUIET))
        }

        // Double push
        val doublePushOrigin = if (us == Color.WHITE)
            ((pawns and startRank) shl 8) and occ.inv()
        else
            ((pawns and startRank) shr 8) and occ.inv()
        val doublePushTargets = if (us == Color.WHITE)
            (doublePushOrigin shl 8) and occ.inv()
        else
            (doublePushOrigin shr 8) and occ.inv()
        doublePushTargets.forEachSquare { to ->
            val from = to - 2 * forwardShift
            out.add(Move.of(Square(from), Square(to), MoveKind.DOUBLE_PUSH))
        }

        // Promotions (push)
        val promoPushPawns = pawns and promoRankFromBelow
        if (promoPushPawns != 0uL) {
            val promoPushTargets = if (us == Color.WHITE) promoPushPawns shl 8 else promoPushPawns shr 8
            val pushable = promoPushTargets and occ.inv()
            pushable.forEachSquare { to ->
                val from = to - forwardShift
                out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_QUEEN))
                out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_ROOK))
                out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_BISHOP))
                out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_KNIGHT))
            }
        }

        // Captures (incl. promotion captures)
        pawns.forEachSquare { from ->
            val attacks = PAWN_ATTACKS[us.ordinal][from] and theirOcc
            attacks.forEachSquare { to ->
                if ((1uL shl to) and (Bitboards.RANK_1 or Bitboards.RANK_8) != 0uL) {
                    out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_QUEEN_CAPTURE))
                    out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_ROOK_CAPTURE))
                    out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_BISHOP_CAPTURE))
                    out.add(Move.of(Square(from), Square(to), MoveKind.PROMO_KNIGHT_CAPTURE))
                } else {
                    out.add(Move.of(Square(from), Square(to), MoveKind.CAPTURE))
                }
            }
        }

        // En passant
        val ep = pos.enPassantSquare
        if (ep != null) {
            val epBb = ep.bitboard
            val attackersFrom = PAWN_ATTACKS[them.ordinal][ep.index] and pawns
            attackersFrom.forEachSquare { from ->
                out.add(Move.of(Square(from), ep, MoveKind.EN_PASSANT))
            }
            // suppress unused warning (epBb is conceptually the target; the check above is enough)
            @Suppress("UNUSED_EXPRESSION") epBb
        }
    }

    // --- Knights -----------------------------------------------------------

    private fun generateKnightMoves(
        pos: Position,
        out: MutableList<Move>,
        us: Color,
        ourOcc: ULong,
        theirOcc: ULong,
    ) {
        val knights = pos.bitboard(Piece.of(us, PieceType.KNIGHT))
        knights.forEachSquare { from ->
            val attacks = KNIGHT_ATTACKS[from] and ourOcc.inv()
            attacks.forEachSquare { to ->
                val isCapture = ((1uL shl to) and theirOcc) != 0uL
                out.add(Move.of(Square(from), Square(to), if (isCapture) MoveKind.CAPTURE else MoveKind.QUIET))
            }
        }
    }

    // --- Bishops, rooks, queens -------------------------------------------

    private fun generateSlidingMoves(
        pos: Position,
        out: MutableList<Move>,
        us: Color,
        ourOcc: ULong,
        theirOcc: ULong,
        occ: ULong,
    ) {
        emitSliderMoves(pos.bitboard(Piece.of(us, PieceType.BISHOP)), out, ourOcc, theirOcc) { sq ->
            bishopAttacks(sq, occ)
        }
        emitSliderMoves(pos.bitboard(Piece.of(us, PieceType.ROOK)), out, ourOcc, theirOcc) { sq ->
            rookAttacks(sq, occ)
        }
        emitSliderMoves(pos.bitboard(Piece.of(us, PieceType.QUEEN)), out, ourOcc, theirOcc) { sq ->
            queenAttacks(sq, occ)
        }
    }

    private inline fun emitSliderMoves(
        pieces: ULong,
        out: MutableList<Move>,
        ourOcc: ULong,
        theirOcc: ULong,
        attacksFor: (Int) -> ULong,
    ) {
        pieces.forEachSquare { from ->
            val attacks = attacksFor(from) and ourOcc.inv()
            attacks.forEachSquare { to ->
                val isCapture = ((1uL shl to) and theirOcc) != 0uL
                out.add(Move.of(Square(from), Square(to), if (isCapture) MoveKind.CAPTURE else MoveKind.QUIET))
            }
        }
    }

    // --- King (non-castling) ----------------------------------------------

    private fun generateKingMoves(
        pos: Position,
        out: MutableList<Move>,
        us: Color,
        them: Color,
        ourOcc: ULong,
        theirOcc: ULong,
        occ: ULong,
    ) {
        val kingBb = pos.bitboard(Piece.of(us, PieceType.KING))
        if (kingBb == 0uL) return
        val from = Bitboards.lsb(kingBb)
        val attacks = KING_ATTACKS[from] and ourOcc.inv()
        attacks.forEachSquare { to ->
            val isCapture = ((1uL shl to) and theirOcc) != 0uL
            out.add(Move.of(Square(from), Square(to), if (isCapture) MoveKind.CAPTURE else MoveKind.QUIET))
        }
    }

    // --- Castling ----------------------------------------------------------

    private fun generateCastlingMoves(
        pos: Position,
        out: MutableList<Move>,
        us: Color,
        them: Color,
        occ: ULong,
    ) {
        val rights = pos.castlingRights
        val kingFromIdx = if (us == Color.WHITE) Square.E1.index else Square.E8.index

        // King in check disqualifies all castling.
        if (pos.isAttacked(kingFromIdx, them)) return

        if (rights.canCastleKingside(us)) {
            val (f, g) = if (us == Color.WHITE) Pair(Square.F1.index, Square.G1.index)
                         else Pair(Square.F8.index, Square.G8.index)
            val between = (1uL shl f) or (1uL shl g)
            if (occ and between == 0uL && !pos.isAttacked(f, them) && !pos.isAttacked(g, them)) {
                out.add(Move.of(Square(kingFromIdx), Square(g), MoveKind.CASTLE_KING))
            }
        }
        if (rights.canCastleQueenside(us)) {
            val (b, c, d) = if (us == Color.WHITE) Triple(Square.B1.index, Square.C1.index, Square.D1.index)
                            else Triple(Square.B8.index, Square.C8.index, Square.D8.index)
            val between = (1uL shl b) or (1uL shl c) or (1uL shl d)
            if (occ and between == 0uL && !pos.isAttacked(c, them) && !pos.isAttacked(d, them)) {
                out.add(Move.of(Square(kingFromIdx), Square(c), MoveKind.CASTLE_QUEEN))
            }
        }
    }
}
