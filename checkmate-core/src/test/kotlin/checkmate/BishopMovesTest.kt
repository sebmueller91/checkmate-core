package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BishopMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `bishop should not move when blocked from all sides from own pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK)
        val validMoves = mutableListOf<Move>()
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 2), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 5), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 2), gameStateWhite))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 5), gameStateWhite))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white bishop should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)
        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(6, 2), capture = Position(6, 2)),
            Move(from = Position(4, 4), to = Position(6, 6), capture = Position(6, 6)),
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 3)),
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(2, 2)),
            Move(from = Position(4, 4), to = Position(2, 6)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black bishop should be able to perform expected moves`() {
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
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 3)),
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(2, 2)),
            Move(from = Position(4, 4), to = Position(2, 6)),
            Move(from = Position(4, 4), to = Position(1, 1), capture = Position(1, 1)),
            Move(from = Position(4, 4), to = Position(1, 7), capture = Position(1, 7)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `white bishop should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.ROOK, color = Player.WHITE)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4,4), gameState)

        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(6, 2), capture = Position(6, 2)),
            Move(from = Position(4, 4), to = Position(6, 6), capture = Position(6, 6)),
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 5), capture = Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black bishop should not move through own or opponent pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.ROOK, color = Player.WHITE)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(4,4), gameState)

        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 5), capture = Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }
}