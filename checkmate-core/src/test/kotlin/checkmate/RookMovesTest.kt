package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RookMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `rook should not move when blocked from all sides from own pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK)
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 0), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 7), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 0), gameStateWhite))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 7), gameStateWhite))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white rook should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.ROOK, color = Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)
        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(6, 4), capture = Position(6, 4)),
            Move(from = Position(4, 4), to = Position(5, 4)),
            Move(from = Position(4, 4), to = Position(3, 4)),
            Move(from = Position(4, 4), to = Position(2, 4)),
            Move(from = Position(4, 4), to = Position(4, 0)),
            Move(from = Position(4, 4), to = Position(4, 1)),
            Move(from = Position(4, 4), to = Position(4, 2)),
            Move(from = Position(4, 4), to = Position(4, 3)),
            Move(from = Position(4, 4), to = Position(4, 5)),
            Move(from = Position(4, 4), to = Position(4, 6)),
            Move(from = Position(4, 4), to = Position(4, 7)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black rook should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)
        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(1, 4), capture = Position(1, 4)),
            Move(from = Position(4, 4), to = Position(2, 4)),
            Move(from = Position(4, 4), to = Position(3, 4)),
            Move(from = Position(4, 4), to = Position(5, 4)),
            Move(from = Position(4, 4), to = Position(4, 0)),
            Move(from = Position(4, 4), to = Position(4, 1)),
            Move(from = Position(4, 4), to = Position(4, 2)),
            Move(from = Position(4, 4), to = Position(4, 3)),
            Move(from = Position(4, 4), to = Position(4, 5)),
            Move(from = Position(4, 4), to = Position(4, 6)),
            Move(from = Position(4, 4), to = Position(4, 7)),
            )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `white rook should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.ROOK, color = Player.WHITE)
                        this[4][2] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[5][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[4][6] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4,4), gameState)

        val expectedMoves = listOf(
            Move(Position(4,4), Position(4,3)),
            Move(Position(4,4), Position(4,5)),
            Move(Position(4,4), Position(4,6), capture = Position(4,6)),
            Move(Position(4,4), Position(3,4), capture = Position(3,4)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black rook should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.ROOK, color = Player.BLACK)
                        this[4][2] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[5][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[3][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[4][6] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4,4), gameState)

        val expectedMoves = listOf(
            Move(Position(4,4), Position(4,3)),
            Move(Position(4,4), Position(4,5)),
            Move(Position(4,4), Position(4,6), capture = Position(4,6)),
            Move(Position(4,4), Position(3,4), capture = Position(3,4)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }
}