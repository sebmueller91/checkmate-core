package checkmate.moves

import checkmate.CheckmateCore
import checkmate.model.*
import checkmate.util.toBitmapGameState
import isValidMove
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import toGameState
import kotlin.test.assertEquals

internal class MoveVerificationTests {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `all moves are valid moves and checking does not alter game state`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][0] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[4][7] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
                lastMove = Move(Position(6, 7), Position(4, 7))
            )
        }

        val moves = checkmateCore.getValidMoves(gameState)
        moves.forEach { move: Move ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(gameStateBitmap.isValidMove(move))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `pawn can not move into opponent player in straight way`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[5][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(5, 3), Position(6, 3)),
            Move(Position(5, 3), Position(6, 3), capture = Position(6,3))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!gameStateBitmap.isValidMove(invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `player can not capture own piece`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 3), Position(2, 3)),
            Move(Position(1, 3), Position(2, 3), capture = Position(2,3)),
            Move(Position(0, 0), Position(0, 1)),
            Move(Position(0, 0), Position(0, 1), capture = Position(0,1)),
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!gameStateBitmap.isValidMove(invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1)),
            Move(Position(3, 3), Position(4, 4)),
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!gameStateBitmap.isValidMove(invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `player can perform move that frees the king from check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                    this[3][4] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }
            )
        }

        val validMoves = listOf(
            Move(Position(1, 2), Position(2, 3), capture = Position(2,3)),
            Move(Position(1, 4), Position(2, 3), capture = Position(2,3)),
            Move(Position(3, 4), Position(2, 3), capture = Position(2,3)),
        )

        validMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(gameStateBitmap.isValidMove(invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }
}