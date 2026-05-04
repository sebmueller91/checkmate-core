package dev.smueller.checkmate.server

import kotlinx.coroutines.channels.Channel

/**
 * Fixed-size pool of long-lived [UciEngine] processes.
 *
 * Engines are checked out via [withEngine] and automatically returned when the
 * block completes.  Each engine resets its game state on every checkout (via
 * `ucinewgame` in [UciEngine.bestMove]) so consecutive games are independent.
 *
 * Callers that exceed the pool capacity suspend until an engine is available.
 */
class EnginePool(val size: Int, private val executablePath: String) : AutoCloseable {

    private val available = Channel<UciEngine>(size)

    init {
        repeat(size) { available.trySend(UciEngine(executablePath)) }
    }

    suspend fun <T> withEngine(block: suspend (UciEngine) -> T): T {
        val engine = available.receive()
        return try {
            block(engine)
        } finally {
            // Return the engine, replacing it if the underlying process died.
            if (engine.isAlive) available.send(engine)
            else available.send(UciEngine(executablePath))
        }
    }

    override fun close() {
        while (true) {
            val r = available.tryReceive()
            if (r.isSuccess) r.getOrThrow().close() else break
        }
    }

    companion object {
        /** Returns the stockfish executable path (env override or PATH lookup), or null. */
        fun findExecutable(): String? {
            val env = System.getenv("STOCKFISH_PATH")
            if (!env.isNullOrBlank()) return env
            return runCatching {
                ProcessBuilder("stockfish").redirectErrorStream(true).start()
                    .also { it.destroy() }
                "stockfish"
            }.getOrNull()
        }
    }
}
