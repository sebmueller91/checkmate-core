package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class KingMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `king should not move when blocked from all sides by own pieces`() {
        val game = checkmateCore.generateInitialState()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK)

        val validMoves = mutableListOf<Move>()
        validMoves.addAll(checkmateCore.getValidMoves(Position(0, 4), gameStateBlack))
        validMoves.addAll(checkmateCore.getValidMoves(Position(7, 4), gameStateWhite))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white king should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[5][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 4), capture = Position(5, 4)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(4, 3)),
            Move(Position(4, 4), Position(4, 5), capture = Position(4, 5)),
            Move(Position(4, 4), Position(3, 3)),
            Move(Position(4, 4), Position(3, 4)),
            Move(Position(4, 4), Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black king should be able to perform expected moves`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[5][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMoves = listOf(
            Move(Position(4, 4), Position(5, 3)),
            Move(Position(4, 4), Position(5, 4), capture = Position(5, 4)),
            Move(Position(4, 4), Position(5, 5)),
            Move(Position(4, 4), Position(4, 3)),
            Move(Position(4, 4), Position(3, 3)),
            Move(Position(4, 4), Position(3, 4)),
            Move(Position(4, 4), Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `white king should be able to perform castling queen side`() {

    }
}