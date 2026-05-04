package dev.smueller.checkmate

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The correctness gate for move generation. Standard positions and node counts
 * from https://www.chessprogramming.org/Perft_Results.
 *
 * Depth 4 is the v0.1 minimum bar. Depth 5+ runs are documented but not
 * gating in v0.1 to keep the test suite fast — a follow-up can flip them on.
 */
class PerftTest {

    @Test
    fun position1_initial_depth1() = check(INITIAL, 1, 20L)

    @Test
    fun position1_initial_depth2() = check(INITIAL, 2, 400L)

    @Test
    fun position1_initial_depth3() = check(INITIAL, 3, 8_902L)

    @Test
    fun position1_initial_depth4() = check(INITIAL, 4, 197_281L)

    @Test
    fun position2_kiwipete_depth1() = check(KIWIPETE, 1, 48L)

    @Test
    fun position2_kiwipete_depth2() = check(KIWIPETE, 2, 2_039L)

    @Test
    fun position2_kiwipete_depth3() = check(KIWIPETE, 3, 97_862L)

    @Test
    fun position2_kiwipete_depth4() = check(KIWIPETE, 4, 4_085_603L)

    @Test
    fun position3_endgame_depth1() = check(POSITION_3, 1, 14L)

    @Test
    fun position3_endgame_depth2() = check(POSITION_3, 2, 191L)

    @Test
    fun position3_endgame_depth3() = check(POSITION_3, 3, 2_812L)

    @Test
    fun position3_endgame_depth4() = check(POSITION_3, 4, 43_238L)

    @Test
    fun position4_promo_depth1() = check(POSITION_4, 1, 6L)

    @Test
    fun position4_promo_depth2() = check(POSITION_4, 2, 264L)

    @Test
    fun position4_promo_depth3() = check(POSITION_4, 3, 9_467L)

    @Test
    fun position4_promo_depth4() = check(POSITION_4, 4, 422_333L)

    @Test
    fun position5_complex_depth1() = check(POSITION_5, 1, 44L)

    @Test
    fun position5_complex_depth2() = check(POSITION_5, 2, 1_486L)

    @Test
    fun position5_complex_depth3() = check(POSITION_5, 3, 62_379L)

    @Test
    fun position5_complex_depth4() = check(POSITION_5, 4, 2_103_487L)

    @Test
    fun position6_depth1() = check(POSITION_6, 1, 46L)

    @Test
    fun position6_depth2() = check(POSITION_6, 2, 2_079L)

    @Test
    fun position6_depth3() = check(POSITION_6, 3, 89_890L)

    @Test
    fun position6_depth4() = check(POSITION_6, 4, 3_894_594L)

    private fun check(fen: String, depth: Int, expected: Long) {
        val pos = Position.fromFen(fen)
        val actual = perft(pos, depth)
        assertEquals(expected, actual, "perft($depth) on $fen")
    }

    companion object {
        const val INITIAL = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        const val KIWIPETE = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"
        const val POSITION_3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1"
        const val POSITION_4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"
        const val POSITION_5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"
        const val POSITION_6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"
    }
}
