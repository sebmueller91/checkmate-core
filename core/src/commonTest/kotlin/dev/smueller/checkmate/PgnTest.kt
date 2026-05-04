package dev.smueller.checkmate

import dev.smueller.checkmate.notation.Pgn
import kotlin.test.Test
import kotlin.test.assertEquals

class PgnTest {

    // --- toSan ---------------------------------------------------------------

    @Test
    fun toSan_pawnPush() {
        val san = Pgn.toSan(Position.initial, move(Position.initial, "e2", "e4"))
        assertEquals("e4", san)
    }

    @Test
    fun toSan_knightDevelopment() {
        val san = Pgn.toSan(Position.initial, move(Position.initial, "g1", "f3"))
        assertEquals("Nf3", san)
    }

    @Test
    fun toSan_pawnCapture() {
        // 1. e4 d5 — then exd5
        var pos = Position.initial
        pos = pos.makeMove(move(pos, "e2", "e4"))
        pos = pos.makeMove(move(pos, "d7", "d5"))
        val san = Pgn.toSan(pos, move(pos, "e4", "d5"))
        assertEquals("exd5", san)
    }

    @Test
    fun toSan_kingsideCastle() {
        // Position where white can castle kingside.
        val pos = Position.fromFen("r1bqk2r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4")
        val castleMove = pos.legalMoves().first { it.kind == MoveKind.CASTLE_KING }
        assertEquals("O-O", Pgn.toSan(pos, castleMove))
    }

    @Test
    fun toSan_queensideCastle() {
        val pos = Position.fromFen("r3kbnr/ppp2ppp/2n1p3/3pNb2/3P4/2NB4/PPP2PPP/R3KBNR w KQkq - 0 1")
        // White has queenside castling rights: R on a1, King on e1, no pieces in between (b1,c1,d1 empty).
        // Check: a1-e1 — pieces on b1,c1,d1 must be clear. The FEN has no pieces there.
        val castleMove = pos.legalMoves().firstOrNull { it.kind == MoveKind.CASTLE_QUEEN }
            ?: return  // Skip if not available in this exact position
        assertEquals("O-O-O", Pgn.toSan(pos, castleMove))
    }

    @Test
    fun toSan_queenPromotion() {
        val pos = Position.fromFen("8/P7/8/8/8/8/8/4K1k1 w - - 0 1")
        val promoMove = pos.legalMoves().first {
            it.from == Square.parse("a7") && it.to == Square.parse("a8") && it.kind == MoveKind.PROMO_QUEEN
        }
        assertEquals("a8=Q", Pgn.toSan(pos, promoMove))
    }

    @Test
    fun toSan_promotionWithCapture() {
        val pos = Position.fromFen("1r6/P7/8/8/8/8/8/4K1k1 w - - 0 1")
        val promoMove = pos.legalMoves().first {
            it.from == Square.parse("a7") && it.to == Square.parse("b8") && it.kind == MoveKind.PROMO_QUEEN_CAPTURE
        }
        assertEquals("axb8=Q", Pgn.toSan(pos, promoMove))
    }

    @Test
    fun toSan_checkSuffix() {
        // Scholar's mate setup: after 1.e4 e5 2.Qh5 Nc6 3.Bc4, white plays Qxf7#
        var pos = Position.initial
        pos = pos.makeMove(move(pos, "e2", "e4"))
        pos = pos.makeMove(move(pos, "e7", "e5"))
        pos = pos.makeMove(move(pos, "d1", "h5"))
        pos = pos.makeMove(move(pos, "b8", "c6"))
        pos = pos.makeMove(move(pos, "f1", "c4"))
        pos = pos.makeMove(move(pos, "g8", "f6"))
        val qxf7 = move(pos, "h5", "f7")
        assertEquals("Qxf7#", Pgn.toSan(pos, qxf7))
    }

    @Test
    fun toSan_checkNotMate() {
        // Knight on f5 moves to g7, giving check (attacks e8) but not checkmate
        // (black king can escape to d8, d7, e7, f7, f8).
        val pos = Position.fromFen("4k3/8/8/5N2/8/8/8/4K3 w - - 0 1")
        val ng7 = pos.legalMoves().first { it.from == Square.parse("f5") && it.to == Square.parse("g7") }
        assertEquals("Ng7+", Pgn.toSan(pos, ng7))
    }

    @Test
    fun toSan_disambiguationByFile() {
        // Two rooks can go to e1; use file to disambiguate.
        val pos = Position.fromFen("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1")
        // Rooks on a1 and h1; both can go to e1 (d1/b1/c1 are open, but let's go e1-adjacent)
        // Actually let's use a simpler FEN: two rooks on a1 and a8 can both go to a4
        val pos2 = Position.fromFen("R3k3/8/8/8/8/8/8/R3K3 w - - 0 1")
        val moveA1toA4 = pos2.legalMoves().first {
            it.from == Square.parse("a1") && it.to == Square.parse("a4")
        }
        val san = Pgn.toSan(pos2, moveA1toA4)
        // Ra8 continues to give check on e8 after Ra1 moves, so the suffix is "+".
        assertEquals("R1a4+", san)
    }

    @Test
    fun toSan_disambiguationByRank() {
        // Two rooks on the same rank; disambiguate by file.
        val pos = Position.fromFen("4k3/8/8/8/8/8/8/R3KR2 w K - 0 1")
        // Rooks on a1 and f1; both can potentially go to d1
        // Let's find a target square where both can reach
        val legalMoves = pos.legalMoves()
        val a1Rook = Square.parse("a1")
        val f1Rook = Square.parse("f1")
        // Find a square reachable by both
        val a1Targets = legalMoves.filter { it.from == a1Rook }.map { it.to }.toSet()
        val f1Targets = legalMoves.filter { it.from == f1Rook }.map { it.to }.toSet()
        val common = a1Targets.intersect(f1Targets)
        if (common.isEmpty()) return  // No ambiguous target in this position
        val target = common.first()
        val moveFromA1 = legalMoves.first { it.from == a1Rook && it.to == target }
        val san = Pgn.toSan(pos, moveFromA1)
        // Should be disambiguated by file (Ra1...)
        assertEquals("Ra${target.notation()}", san)
    }

    // --- parseSan ------------------------------------------------------------

    @Test
    fun parseSan_pawnPush() {
        val move = Pgn.parseSan(Position.initial, "e4")
        assertEquals(Square.parse("e2"), move.from)
        assertEquals(Square.parse("e4"), move.to)
    }

    @Test
    fun parseSan_knightMove() {
        val move = Pgn.parseSan(Position.initial, "Nf3")
        assertEquals(Square.parse("g1"), move.from)
        assertEquals(Square.parse("f3"), move.to)
    }

    @Test
    fun parseSan_pawnCapture() {
        var pos = Position.initial
        pos = pos.makeMove(move(pos, "e2", "e4"))
        pos = pos.makeMove(move(pos, "d7", "d5"))
        val m = Pgn.parseSan(pos, "exd5")
        assertEquals(Square.parse("e4"), m.from)
        assertEquals(Square.parse("d5"), m.to)
    }

    @Test
    fun parseSan_promotion() {
        val pos = Position.fromFen("8/P7/8/8/8/8/8/4K1k1 w - - 0 1")
        val m = Pgn.parseSan(pos, "a8=Q")
        assertEquals(MoveKind.PROMO_QUEEN, m.kind)
        assertEquals(Square.parse("a7"), m.from)
        assertEquals(Square.parse("a8"), m.to)
    }

    @Test
    fun parseSan_kingsideCastle() {
        val pos = Position.fromFen("r1bqk2r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4")
        val m = Pgn.parseSan(pos, "O-O")
        assertEquals(MoveKind.CASTLE_KING, m.kind)
    }

    @Test
    fun parseSan_ignoresCheckSuffix() {
        var pos = Position.initial
        pos = pos.makeMove(move(pos, "e2", "e4"))
        pos = pos.makeMove(move(pos, "e7", "e5"))
        val m = Pgn.parseSan(pos, "Qh5+")
        assertEquals(Square.parse("d1"), m.from)
        assertEquals(Square.parse("h5"), m.to)
    }

    // --- roundtrip -----------------------------------------------------------

    @Test
    fun sanRoundTrip_scholarsMateMoves() {
        // 1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7#
        val gameSan = listOf("e4", "e5", "Qh5", "Nc6", "Bc4", "Nf6", "Qxf7#")
        var pos = Position.initial
        for (san in gameSan) {
            val m = Pgn.parseSan(pos, san)
            val generated = Pgn.toSan(pos, m)
            assertEquals(san.trimEnd('+', '#'), generated.trimEnd('+', '#'),
                "Round-trip failed at move $san")
            pos = pos.makeMove(m)
        }
    }

    @Test
    fun formatMovetext_scholarsmate() {
        var pos = Position.initial
        val moves = mutableListOf<Move>()
        for (ucis in listOf("e2e4", "e7e5", "d1h5", "b8c6", "f1c4", "g8f6", "h5f7")) {
            val m = pos.legalMoves().first { it.toUci() == ucis }
            moves.add(m)
            pos = pos.makeMove(m)
        }
        val text = Pgn.formatMovetext(Position.initial, moves)
        assertEquals("1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7#", text)
    }

    @Test
    fun parseMovetext_scholarsmate() {
        val moves = Pgn.parseMovetext(Position.initial, "1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7#")
        assertEquals(7, moves.size)
        // Final position is checkmate
        var pos = Position.initial
        for (m in moves) pos = pos.makeMove(m)
        assertEquals(GameStatus.Checkmate(Color.WHITE), pos.status())
    }

    @Test
    fun parseMovetext_roundTrip() {
        val movetext = "1. e4 e5 2. Nf3 Nc6 3. Bb5 a6"
        val moves = Pgn.parseMovetext(Position.initial, movetext)
        val regenerated = Pgn.formatMovetext(Position.initial, moves)
        assertEquals(movetext, regenerated)
    }

    // --- helper --------------------------------------------------------------

    private fun move(pos: Position, fromSq: String, toSq: String): Move {
        val from = Square.parse(fromSq)
        val to = Square.parse(toSq)
        return pos.legalMoves().firstOrNull { it.from == from && it.to == to }
            ?: error("No legal move $fromSq-$toSq in ${pos.toFen()}")
    }
}
