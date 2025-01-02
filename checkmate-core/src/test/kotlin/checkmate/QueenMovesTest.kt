package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class QueenMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `queen should not move when blocked from all sides by own pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK)

        val validMoves = mutableListOf<Move>()
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 3), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 3), gameStateWhite))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white queen should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            // Vertical
            Move(Position(4, 4), Position(2, 4)),
            Move(Position(4, 4), Position(3, 4)),
            Move(Position(4, 4), Position(5, 4)),
            Move(Position(4, 4), Position(6, 4), capture = Position(6, 4)),

            // Horizontal
            Move(Position(4, 4), Position(4, 0)),
            Move(Position(4, 4), Position(4, 1)),
            Move(Position(4, 4), Position(4, 2)),
            Move(Position(4, 4), Position(4, 3)),
            Move(Position(4, 4), Position(4, 5)),
            Move(Position(4, 4), Position(4, 6)),
            Move(Position(4, 4), Position(4, 7)),

            // Diagonal
            Move(Position(4, 4), Position(6, 2), capture = Position(6, 2)),
            Move(Position(4, 4), Position(6, 6), capture = Position(6, 6)),
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(3, 3)),
            Move(Position(4, 4), Position(3, 5)),
            Move(Position(4, 4), Position(2, 6)),
            Move(Position(4, 4), Position(2, 2))
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black queen should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            // Vertical
            Move(Position(4, 4), Position(1, 4), capture = Position(1, 4)),
            Move(Position(4, 4), Position(2, 4)),
            Move(Position(4, 4), Position(3, 4)),
            Move(Position(4, 4), Position(5, 4)),

            // Horizontal
            Move(Position(4, 4), Position(4, 0)),
            Move(Position(4, 4), Position(4, 1)),
            Move(Position(4, 4), Position(4, 2)),
            Move(Position(4, 4), Position(4, 3)),
            Move(Position(4, 4), Position(4, 5)),
            Move(Position(4, 4), Position(4, 6)),
            Move(Position(4, 4), Position(4, 7)),

            // Diagonal
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(3, 3)),
            Move(Position(4, 4), Position(3, 5)),
            Move(Position(4, 4), Position(2, 2)),
            Move(Position(4, 4), Position(2, 6)),
            Move(Position(4, 4), Position(1, 1), capture = Position(1, 1)),
            Move(Position(4, 4), Position(1, 7), capture = Position(1, 7)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `white queen should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.QUEEN, color = Player.WHITE)
                        this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[4][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[3][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            // Vertical
            Move(Position(4, 4), Position(5, 4)),
            Move(Position(4, 4), Position(6, 4), capture = Position(6, 4)),

            // Horizontal
            Move(Position(4, 4), Position(4, 3), capture = Position(4, 3)),

            // Diagonal
            Move(Position(4, 4), Position(6, 2), capture = Position(6, 2)),
            Move(Position(4, 4), Position(6, 6), capture = Position(6, 6)),
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(3, 3), capture = Position(3,3)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black queen should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.QUEEN, color = Player.BLACK)
                        this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[4][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[3][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            // Vertical
            Move(Position(4, 4), Position(5, 4)),
            Move(Position(4, 4), Position(3, 4), capture = Position(3, 4)),

            // Horizontal
            Move(Position(4, 4), Position(4, 5), capture = Position(4, 5)),

            // Diagonal
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(3, 5), capture = Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }
}