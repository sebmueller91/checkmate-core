package dev.smueller.checkmate.server

import dev.smueller.checkmate.Color
import java.time.Clock
import java.time.Duration
import java.time.Instant

data class ClockConfig(val initialMs: Long, val incrementMs: Long)

class GameClock(
    val initialMs: Long,
    val incrementMs: Long,
    private val clock: Clock = Clock.systemUTC(),
) {
    var whiteMs: Long = initialMs
        private set
    var blackMs: Long = initialMs
        private set

    private var lastTickAt: Instant = Instant.now(clock)

    fun start() {
        lastTickAt = Instant.now(clock)
    }

    /**
     * Records a completed move for [color]. Deducts elapsed time and adds increment.
     * Returns the remaining time in ms for [color] after this move (negative = flagged).
     */
    fun recordMove(color: Color): Long {
        val now = Instant.now(clock)
        val elapsed = Duration.between(lastTickAt, now).toMillis()
        lastTickAt = now
        return when (color) {
            Color.WHITE -> { whiteMs = whiteMs - elapsed + incrementMs; whiteMs }
            Color.BLACK -> { blackMs = blackMs - elapsed + incrementMs; blackMs }
        }
    }

    fun remaining(color: Color): Long = if (color == Color.WHITE) whiteMs else blackMs
}
