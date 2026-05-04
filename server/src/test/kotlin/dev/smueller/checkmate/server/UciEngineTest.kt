package dev.smueller.checkmate.server

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UciEngineTest {

    private val stockfishPath = EnginePool.findExecutable()

    /** Skip gracefully when stockfish is not installed. */
    private fun assumeStockfish() {
        if (stockfishPath == null) {
            println("Skipping UciEngineTest — stockfish not found (set STOCKFISH_PATH to enable)")
        }
    }

    @Test
    fun engineReturnsLegalMove() {
        assumeStockfish(); if (stockfishPath == null) return

        runBlocking {
            UciEngine(stockfishPath).use { engine ->
                val move = engine.bestMove(
                    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                    movetimeMs = 200,
                    skillLevel = 20,
                )
                assertTrue(move.length in 4..5, "Expected UCI move of 4-5 chars, got '$move'")
            }
        }
    }

    @Test
    fun poolServesConcurrentGames() {
        assumeStockfish(); if (stockfishPath == null) return

        runBlocking {
            EnginePool(size = 4, executablePath = stockfishPath!!).use { pool ->
                val fens = List(10) {
                    "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
                }
                val moves = fens.map { fen ->
                    pool.withEngine { engine ->
                        engine.bestMove(fen, movetimeMs = 200, skillLevel = 10)
                    }
                }
                assertTrue(moves.all { it.length in 4..5 }, "Some moves were invalid: $moves")
            }
        }
    }
}
