package dev.smueller.checkmate

import kotlin.test.Test
import kotlin.test.assertEquals

class FenRoundTripTest {

    @Test
    fun initialPosition() = roundTrip("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")

    @Test
    fun kiwipete() = roundTrip("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1")

    @Test
    fun position3() = roundTrip("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1")

    @Test
    fun position4() = roundTrip("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1")

    @Test
    fun positionWithEnPassant() {
        // After 1.e4 the EP target square is e3.
        val initial = Position.initial
        val e2 = Square.parse("e2")
        val e4 = Square.parse("e4")
        val move = initial.legalMoves().single { it.from == e2 && it.to == e4 }
        val after = initial.makeMove(move)
        assertEquals("e3", after.enPassantSquare?.notation())
        roundTrip(after.toFen())
    }

    @Test
    fun blackToMove() = roundTrip("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1")

    @Test
    fun noCastlingRights() = roundTrip("4k3/8/8/8/8/8/8/4K3 w - - 0 1")

    @Test
    fun whitePromotionThreeKings() {
        // Stress: many minor pieces, mid-fullmove counter.
        roundTrip("r1bqkb1r/pppp1ppp/2n2n2/4p3/4P3/2N2N2/PPPP1PPP/R1BQKB1R w KQkq - 4 4")
    }

    private fun roundTrip(fen: String) {
        val pos = Position.fromFen(fen)
        assertEquals(fen, pos.toFen(), "FEN round-trip mismatch")
    }
}
