package dev.smueller.checkmate

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GameStatusTest {

    @Test
    fun startingPositionIsOngoing() {
        assertEquals(GameStatus.Ongoing, Position.initial.status())
    }

    @Test
    fun foolsMate_blackWins() {
        // 1. f3 e5 2. g4 Qh4#
        var pos = Position.initial
        pos = play(pos, "f2", "f3", MoveKind.QUIET)
        pos = play(pos, "e7", "e5", MoveKind.DOUBLE_PUSH)
        pos = play(pos, "g2", "g4", MoveKind.DOUBLE_PUSH)
        pos = play(pos, "d8", "h4", MoveKind.QUIET)
        assertEquals(GameStatus.Checkmate(Color.BLACK), pos.status())
    }

    @Test
    fun stalemate_classicCorner() {
        // Black king on a8 has no legal moves: queen on b6 covers a7, b7, b8;
        // a8 itself is not attacked (so not in check).
        val pos = Position.fromFen("k7/8/1Q6/2K5/8/8/8/8 b - - 0 1")
        assertEquals(GameStatus.Draw(DrawReason.STALEMATE), pos.status())
    }

    @Test
    fun fiftyMoveRule_drawn() {
        // Halfmove clock at 100 with a position where moves remain.
        val pos = Position.fromFen("4k3/8/4K3/4Q3/8/8/8/8 w - - 100 60")
        assertEquals(GameStatus.Draw(DrawReason.FIFTY_MOVE_RULE), pos.status())
    }

    @Test
    fun insufficientMaterial_kingVsKing() {
        val pos = Position.fromFen("4k3/8/8/8/8/8/8/4K3 w - - 0 1")
        assertEquals(GameStatus.Draw(DrawReason.INSUFFICIENT_MATERIAL), pos.status())
    }

    @Test
    fun insufficientMaterial_kingPlusBishopVsKing() {
        val pos = Position.fromFen("4k3/8/8/8/8/8/8/3BK3 w - - 0 1")
        assertEquals(GameStatus.Draw(DrawReason.INSUFFICIENT_MATERIAL), pos.status())
    }

    @Test
    fun insufficientMaterial_kingPlusKnightVsKing() {
        val pos = Position.fromFen("4k3/8/8/8/8/8/8/3NK3 w - - 0 1")
        assertEquals(GameStatus.Draw(DrawReason.INSUFFICIENT_MATERIAL), pos.status())
    }

    @Test
    fun insufficientMaterial_bishopsSameColor() {
        // Both bishops on light squares (f1, c8 — both light).
        val pos = Position.fromFen("2b1k3/8/8/8/8/8/8/4KB2 w - - 0 1")
        assertEquals(GameStatus.Draw(DrawReason.INSUFFICIENT_MATERIAL), pos.status())
    }

    @Test
    fun sufficientMaterial_kingPlusRookVsKing() {
        val pos = Position.fromFen("4k3/8/4K3/8/8/8/8/4R3 w - - 0 1")
        assertEquals(GameStatus.Ongoing, pos.status())
    }

    @Test
    fun threefoldRepetition_viaHistory() {
        val pos = Position.initial
        val history = listOf(pos.zobristHash, pos.zobristHash) // current + 2 = 3 occurrences
        assertEquals(
            GameStatus.Draw(DrawReason.THREEFOLD_REPETITION),
            pos.statusWithHistory(history)
        )
    }

    @Test
    fun threefoldRepetition_belowThresholdIsOngoing() {
        val pos = Position.initial
        val history = listOf(pos.zobristHash) // current + 1 = 2 occurrences
        assertEquals(GameStatus.Ongoing, pos.statusWithHistory(history))
    }

    private fun play(pos: Position, fromSq: String, toSq: String, expectedKind: MoveKind): Position {
        val from = Square.parse(fromSq)
        val to = Square.parse(toSq)
        val move = pos.legalMoves().firstOrNull { it.from == from && it.to == to && it.kind == expectedKind }
            ?: pos.legalMoves().firstOrNull { it.from == from && it.to == to }
            ?: error("No legal move $fromSq-$toSq from ${pos.toFen()}")
        return pos.makeMove(move)
    }
}
