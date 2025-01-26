package checkmate.moves.pseudolegal

import checkmate.CheckmateCoreImpl
import checkmate.model.*
import checkmate.moves.type.QueenMoves
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class QueenPseudoLegalMovesTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `queen should not move when blocked from all sides by own pieces`() {
        val game = checkmateCore.getInitialGame()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE).toBitmapGameState()
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK).toBitmapGameState()

        val validMoves = mutableListOf<Move>()
        validMoves.addAll(QueenMoves.generatePseudoLegalMoves(gameStateBlack, Position(7,3)))
        validMoves.addAll(QueenMoves.generatePseudoLegalMoves(gameStateWhite, Position(0,3)))
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white queen should be able to perform expected moves`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = QueenMoves.generatePseudoLegalMoves(gameState, Position(4,4))
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
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = QueenMoves.generatePseudoLegalMoves(gameState, Position(4,4))
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
        val game = checkmateCore.getInitialGame()
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
        }.toBitmapGameState()

        val validMoves = QueenMoves.generatePseudoLegalMoves(gameState, Position(4,4))
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
            Move(Position(4, 4), Position(3, 3), capture = Position(3, 3)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black queen should not move through own or opponent pieces`() {
        val game = checkmateCore.getInitialGame()
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
        }.toBitmapGameState()

        val validMoves = QueenMoves.generatePseudoLegalMoves(gameState, Position(4,4))
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