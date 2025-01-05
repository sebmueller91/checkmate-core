package checkmate.moves.pseudolegal

import checkmate.CheckmateCore
import checkmate.model.*
import checkmate.moves.KnightMoves
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KnightPseudoLegalMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `knight should not move when when all possible cells are blocked`() {
        val game = checkmateCore.generateInitialState()
        val gameStateBlack = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[5][0] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[5][2] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[5][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[5][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }.toBitmapGameState()
        val gameStateWhite = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[2][0] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[2][2] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[2][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[2][7] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }.toBitmapGameState()

        val validMoves = mutableListOf<Move>()
        validMoves.addAll(KnightMoves.generatePseudoLegalMoves(gameStateWhite, Position(0,1)))
        validMoves.addAll(KnightMoves.generatePseudoLegalMoves(gameStateWhite, Position(0,6)))
        validMoves.addAll(KnightMoves.generatePseudoLegalMoves(gameStateBlack, Position(7,1)))
        validMoves.addAll(KnightMoves.generatePseudoLegalMoves(gameStateBlack, Position(7,6)))
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white knight should move and capture correct cells`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[4][4] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                    })
        }.toBitmapGameState()

        val validMoves = KnightMoves.generatePseudoLegalMoves(gameState, Position(4,4))

        val expectedMoves = listOf(
            Move(Position(4, 4), Position(6, 3), capture = Position(6, 3)),
            Move(Position(4, 4), Position(6, 5), capture = Position(6, 5)),
            Move(Position(4, 4), Position(5, 2)),
            Move(Position(4, 4), Position(5, 6)),
            Move(Position(4, 4), Position(3, 2)),
            Move(Position(4, 4), Position(3, 6)),
            Move(Position(4, 4), Position(2, 3)),
            Move(Position(4, 4), Position(2, 5)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }

    @Test
    fun `black knight should move and capture correct cells`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[3][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                    })
        }.toBitmapGameState()

        val validMoves = KnightMoves.generatePseudoLegalMoves(gameState, Position(3,3))

        val expectedMoves = listOf(
            Move(Position(3, 3), Position(5, 2)),
            Move(Position(3, 3), Position(5, 4)),
            Move(Position(3, 3), Position(4, 1)),
            Move(Position(3, 3), Position(4, 5)),
            Move(Position(3, 3), Position(2, 1)),
            Move(Position(3, 3), Position(2, 5)),
            Move(Position(3, 3), Position(1, 2), capture = Position(1, 2)),
            Move(Position(3, 3), Position(1, 4), capture = Position(1, 4)),
        )

        assertEquals(expectedMoves.toSet(), validMoves.toSet())
    }
}