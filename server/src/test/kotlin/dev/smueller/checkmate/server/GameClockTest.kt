package dev.smueller.checkmate.server

import dev.smueller.checkmate.Color
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** A [Clock] whose instant can be advanced manually in tests. */
private class SettableClock(private var instant: Instant) : Clock() {
    fun advance(ms: Long) { instant = instant.plusMillis(ms) }
    override fun instant() = instant
    override fun getZone() = ZoneOffset.UTC
    override fun withZone(zone: java.time.ZoneId?): Clock = this
}

class GameClockTest {

    @Test
    fun remainingStartsAtInitialTime() {
        val clock = GameClock(60_000L, 0L)
        assertEquals(60_000L, clock.remaining(Color.WHITE))
        assertEquals(60_000L, clock.remaining(Color.BLACK))
    }

    @Test
    fun recordMoveDeductsElapsedTime() {
        val fakeClock = SettableClock(Instant.EPOCH)
        val gameClock = GameClock(60_000L, 0L, fakeClock)
        gameClock.start()
        fakeClock.advance(5_000L)  // white takes 5 seconds
        val remaining = gameClock.recordMove(Color.WHITE)
        assertEquals(55_000L, remaining)
        assertEquals(55_000L, gameClock.remaining(Color.WHITE))
        assertEquals(60_000L, gameClock.remaining(Color.BLACK))  // black untouched
    }

    @Test
    fun recordMoveAddsIncrement() {
        val fakeClock = SettableClock(Instant.EPOCH)
        val gameClock = GameClock(60_000L, 2_000L, fakeClock)
        gameClock.start()
        fakeClock.advance(3_000L)
        val remaining = gameClock.recordMove(Color.WHITE)
        // 60000 - 3000 + 2000 = 59000
        assertEquals(59_000L, remaining)
    }

    @Test
    fun recordMoveAlternatesBetweenSides() {
        val fakeClock = SettableClock(Instant.EPOCH)
        val gameClock = GameClock(60_000L, 0L, fakeClock)
        gameClock.start()

        fakeClock.advance(4_000L)
        gameClock.recordMove(Color.WHITE)  // white: 56000

        fakeClock.advance(6_000L)
        gameClock.recordMove(Color.BLACK)  // black: 54000

        assertEquals(56_000L, gameClock.remaining(Color.WHITE))
        assertEquals(54_000L, gameClock.remaining(Color.BLACK))
    }

    @Test
    fun recordMoveReturnsNegativeWhenFlagged() {
        val fakeClock = SettableClock(Instant.EPOCH)
        val gameClock = GameClock(5_000L, 0L, fakeClock)
        gameClock.start()
        fakeClock.advance(7_000L)  // exceeds the 5 second budget
        val remaining = gameClock.recordMove(Color.WHITE)
        assertTrue(remaining < 0, "Expected negative remaining, got $remaining")
    }

    @Test
    fun startResetsLastTickAt() {
        val fakeClock = SettableClock(Instant.EPOCH)
        val gameClock = GameClock(60_000L, 0L, fakeClock)
        fakeClock.advance(10_000L)  // 10s pass before start()
        gameClock.start()           // resets the reference point
        fakeClock.advance(2_000L)   // only 2s taken for this move
        val remaining = gameClock.recordMove(Color.WHITE)
        assertEquals(58_000L, remaining)  // 60000 - 2000 (not 60000 - 12000)
    }
}
