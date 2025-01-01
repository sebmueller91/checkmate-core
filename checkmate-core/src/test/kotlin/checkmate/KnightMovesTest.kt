package checkmate

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class KnightMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `black rook should not move when blocked from all sides from own pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK)
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 0), gameStateWhite))
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 7), gameStateWhite))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 0), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 7), gameStateBlack))

        assertTrue(validMoves.isEmpty())
    }
}