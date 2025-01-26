package checkmate.moves

import checkmate.CheckmateCoreImpl
import checkmate.model.*
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import toGameState
import kotlin.test.assertEquals

internal class MoveVerificationTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `possible moves are legal and checking does not alter game state`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][6] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
                lastMove = Move(Position(6, 7), Position(4, 7))
            )
        }

        val moves = listOf(
            Move(Position(4, 6), Position(5, 7), capture = Position(4, 7)),
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1)),
        )
        moves.forEach { move: Move ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(isLegalMove(gameStateBitmap, move))
            assertEquals(gameState, gameStateBitmap.toGameState(Move(Position(6, 7), Position(4, 7))))
        }
    }

    @Test
    fun `move is invalid when own king is in check by knight`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
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
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check by bishop`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][6] = Piece(type = Type.BISHOP, color = Player.BLACK)
                    this[1][5] = null
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check by queen`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][6] = Piece(type = Type.QUEEN, color = Player.BLACK)
                    this[1][5] = null
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check by rook`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][4] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[1][4] = null
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check by other king`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[1][4] = Piece(type = Type.KING, color = Player.BLACK)
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `move is invalid when own king is in check by pawn`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }
            )
        }

        val invalidMoves = listOf(
            Move(Position(1, 1), Position(2, 1)),
            Move(Position(1, 1), Position(3, 1))
        )

        invalidMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(!isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }

    @Test
    fun `player can perform move that frees the king from check`() {
        val game = checkmateCore.getInitialGame()
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
            Move(Position(1, 2), Position(2, 3), capture = Position(2, 3)),
            Move(Position(1, 4), Position(2, 3), capture = Position(2, 3)),
            Move(Position(3, 4), Position(2, 3), capture = Position(2, 3)),
        )

        validMoves.forEach { invalidMove ->
            val gameStateBitmap = gameState.toBitmapGameState()
            assert(isLegalMove(gameStateBitmap, invalidMove))
            assertEquals(gameState, gameStateBitmap.toGameState(null))
        }
    }
}