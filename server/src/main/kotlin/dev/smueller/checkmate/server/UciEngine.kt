package dev.smueller.checkmate.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

class UciEngine(executablePath: String) : AutoCloseable {

    private val process: Process = ProcessBuilder(executablePath)
        .redirectErrorStream(true)
        .start()
    private val reader = BufferedReader(InputStreamReader(process.inputStream))
    private val writer = PrintWriter(process.outputStream, true)

    init {
        send("uci")
        readUntil("uciok")
        send("isready")
        readUntil("readyok")
    }

    /**
     * Returns the UCI best-move string (e.g. "e2e4") for [fen] with the given
     * time budget and Stockfish skill level (0–20).
     *
     * Runs on [Dispatchers.IO] to avoid blocking the calling coroutine.
     */
    suspend fun bestMove(fen: String, movetimeMs: Int, skillLevel: Int): String =
        withContext(Dispatchers.IO) {
            // Reset engine state before each use so pool members stay clean.
            send("ucinewgame")
            send("isready")
            readUntil("readyok")
            send("setoption name Skill Level value $skillLevel")
            send("position fen $fen")
            send("go movetime $movetimeMs")
            val line = readUntil("bestmove")
            val move = line.removePrefix("bestmove ").split(" ").first()
            check(move.isNotBlank() && move != "(none)") {
                "Engine returned no move for position: $fen"
            }
            move
        }

    val isAlive: Boolean get() = process.isAlive

    private fun send(cmd: String) = writer.println(cmd)

    private fun readUntil(prefix: String): String {
        while (true) {
            val line = reader.readLine()
                ?: error("Engine process closed unexpectedly while waiting for '$prefix'")
            if (line.startsWith(prefix)) return line
        }
    }

    override fun close() {
        runCatching { send("quit") }
        runCatching { process.destroyForcibly() }
    }
}
