package checkmate

import checkmate.exception.InvalidMoveException
import checkmate.model.GameStatus
import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.InvalidParameterException
import kotlin.test.assertEquals

internal class CheckmateCoreTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `isValidMove returns true for valid move`() {
        val game = checkmateCore.getInitialGame()
        val move = Move(from = Position(1, 2), to = Position(3, 2))

        val result = checkmateCore.isValidMove(move = move, gameState = game.gameStates.last())

        assertEquals(true, result)
    }

    @Test
    fun `isValidMove returns false for invalid move`() {
        val game = checkmateCore.getInitialGame()
        val move = Move(from = Position(1, 2), to = Position(4, 2))

        val result = checkmateCore.isValidMove(move = move, gameState = game.gameStates.last())

        assertEquals(false, result)
    }

    @Test
    fun `executing invalid move throws invalidMoveException`() {
        val game = checkmateCore.getInitialGame()
        val move = Move(from = Position(1, 2), to = Position(4, 2))

        assertThrows<InvalidMoveException> {
            checkmateCore.executeMove(move = move, game = game)
        }
    }

    @Test
    fun `executing move appends game state to the end of the gameStates list`() {
        val game = checkmateCore.getInitialGame()
        val initialGameState = game.gameStates.last()
        val move = Move(from = Position(1,2), to = Position(3,2))

        val result = checkmateCore.executeMove(move = move, game = game)

        val expectedGameState = initialGameState.copy(
            currentPlayer = Player.BLACK,
            board = initialGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][2] = this[1][2]
                    this[1][2] = null
                },
            lastMove = move,
            fullMoveNumber = initialGameState.fullMoveNumber,
            halfMoveClock = 1,
            gameStatus = GameStatus.ONGOING)


        assertEquals(2, result.gameStates.size)
        assertEquals(initialGameState, result.gameStates.first())
        assertEquals(expectedGameState, result.gameStates.last())
    }

    @Test
    fun `executing move with index appends game state in correct position and deletes subsequent game states`() {
        val game = checkmateCore.getInitialGame()
        val initialGameState = game.gameStates.last()
        val oldMove1 = Move(from = Position(1,4), to = Position(3,4))
        val oldMove2 = Move(from = Position(6,2), to = Position(4,2))
        val oldMove3 = Move(from = Position(0,5), to = Position(3,2))

        val oldResult1 = checkmateCore.executeMove(move = oldMove1, game = game)
        val oldResult2 = checkmateCore.executeMove(move = oldMove2, game = oldResult1)
        val oldResult3 = checkmateCore.executeMove(move = oldMove3, game = oldResult2)

        val oldExpectedGameState = initialGameState.copy(
            currentPlayer = Player.BLACK,
            board = initialGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = this[1][4]
                    this[1][4] = null
                    this[4][2] = this[6][2]
                    this[6][2] = null
                    this[3][2] = this[0][5]
                    this[0][5] = null
                },
            lastMove = oldMove3,
            fullMoveNumber = initialGameState.fullMoveNumber+1,
            halfMoveClock = 3,
            gameStatus = GameStatus.ONGOING)

        assertEquals(4, oldResult3.gameStates.size)
        assertEquals(initialGameState, oldResult3.gameStates.first())
        assertEquals(oldExpectedGameState, oldResult3.gameStates.last())

        val newMove = Move(from = Position(1,2), to = Position(3,2))

        val newResult = checkmateCore.executeMove(move = newMove, game = game, moveIndex = 0)

        val newExpectedGameState = initialGameState.copy(
            currentPlayer = Player.BLACK,
            board = initialGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][2] = this[1][2]
                    this[1][2] = null
                },
            lastMove = newMove,
            fullMoveNumber = initialGameState.fullMoveNumber,
            halfMoveClock = 1,
            gameStatus = GameStatus.ONGOING)


        assertEquals(2, newResult.gameStates.size)
        assertEquals(initialGameState, newResult.gameStates.first())
        assertEquals(newExpectedGameState, newResult.gameStates.last())
    }

    @Test
    fun `executing move with too high index throws InvalidParameterException`() {
        val game = checkmateCore.getInitialGame()
        val move = Move(from = Position(1,4), to = Position(3,4))

        assertThrows<InvalidParameterException> {
            checkmateCore.executeMove(move = move, game = game, moveIndex = 5)
        }
    }

    @Test
    fun `executing move with too low index throws InvalidParameterException`() {
        val game = checkmateCore.getInitialGame()
        val move = Move(from = Position(1,4), to = Position(3,4))

        assertThrows<InvalidParameterException> {
            checkmateCore.executeMove(move = move, game = game, moveIndex = -1)
        }
    }

    @Test
    fun `executing move with current index does the same as not providing index`() {
        val game = checkmateCore.getInitialGame()
        val initialGameState = game.gameStates.last()
        val move = Move(from = Position(1,2), to = Position(3,2))

        val result = checkmateCore.executeMove(move = move, game = game, moveIndex = 0)

        val expectedGameState = initialGameState.copy(
            currentPlayer = Player.BLACK,
            board = initialGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][2] = this[1][2]
                    this[1][2] = null
                },
            lastMove = move,
            fullMoveNumber = initialGameState.fullMoveNumber,
            halfMoveClock = 1,
            gameStatus = GameStatus.ONGOING)


        assertEquals(2, result.gameStates.size)
        assertEquals(initialGameState, result.gameStates.first())
        assertEquals(expectedGameState, result.gameStates.last())
    }
}