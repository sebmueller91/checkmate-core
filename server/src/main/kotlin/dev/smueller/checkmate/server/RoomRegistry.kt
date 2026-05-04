package dev.smueller.checkmate.server

import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

class RoomRegistry {
    private val rooms = ConcurrentHashMap<String, GameRoom>()
    private val rng = SecureRandom()

    fun create(clock: ClockConfig? = null): GameRoom {
        val code = generateCode()
        val room = GameRoom(code, clock)
        rooms[code] = room
        return room
    }

    fun find(code: String): GameRoom? = rooms[code]

    fun remove(code: String) { rooms.remove(code) }

    private fun generateCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars[rng.nextInt(chars.length)] }.joinToString("")
    }
}
