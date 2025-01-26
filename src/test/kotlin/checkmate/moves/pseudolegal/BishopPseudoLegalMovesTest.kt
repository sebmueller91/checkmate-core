package checkmate.moves.pseudolegal

import checkmate.CheckmateCoreImpl
import checkmate.model.*
import checkmate.moves.type.BishopMoves
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class BishopPseudoLegalMovesTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `bishop should not move when blocked from all sides from own pieces`() {
        val game = checkmateCore.getInitialGame()
        val gameStateWhite = game.gameStates.last().copy(currentPlayer = Player.WHITE).toBitmapGameState()
        val gameStateBlack = game.gameStates.last().copy(currentPlayer = Player.BLACK).toBitmapGameState()

        val validMoves = mutableListOf<Move>()
        validMoves.addAll(BishopMoves.generatePseudoLegalMoves(gameStateWhite, Position(0,2)))
        validMoves.addAll(BishopMoves.generatePseudoLegalMoves(gameStateWhite, Position(0,5)))
        validMoves.addAll(BishopMoves.generatePseudoLegalMoves(gameStateBlack, Position(7,2)))
        validMoves.addAll(BishopMoves.generatePseudoLegalMoves(gameStateBlack, Position(7,5)))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white bishop should be able to perform expected moves`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = BishopMoves.generatePseudoLegalMoves(gameState, Position(4,4))

        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(6, 2), capture = Position(6, 2)),
            Move(from = Position(4, 4), to = Position(6, 6), capture = Position(6, 6)),
            Move(from = Position(4, 4), to = Position(5, 3)),
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
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = BishopMoves.generatePseudoLegalMoves(gameState, Position(4,4))

        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(5, 3)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 3)),
            Move(from = Position(4, 4), to = Position(3, 5)),
            Move(from = Position(4, 4), to = Position(2, 2)),
            Move(from = Position(4, 4), to = Position(2, 6)),
            Move(from = Position(4, 4), to = Position(1, 1), capture = Position(1, 1)),
            Move(from = Position(4, 4), to = Position(1, 7), capture = Position(1, 7)),
        )

        val a = validMoves.toSet() - expectedMoves.toSet()
        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `white bishop should not move through own or opponent pieces`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.BISHOP, color = Player.WHITE)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }.toBitmapGameState()

        val validMoves = BishopMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(6, 2), capture = Position(6, 2)),
            Move(from = Position(4, 4), to = Position(6, 6), capture = Position(6, 6)),
            Move(from = Position(4, 4), to = Position(5, 3)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 5), capture = Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black bishop should not move through own or opponent pieces`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.BISHOP, color = Player.BLACK)
                        this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }.toBitmapGameState()

        val validMoves = BishopMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMoves = listOf(
            Move(from = Position(4, 4), to = Position(5, 3)),
            Move(from = Position(4, 4), to = Position(5, 5)),
            Move(from = Position(4, 4), to = Position(3, 5), capture = Position(3, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }
}