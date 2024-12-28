package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class CheckmatePawnTests {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `getValidMoves should return an empty list for an empty field`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last()
        val emptyPosition = Position(4, 4)
        val validMoves = checkmateCore.getValidMoves(emptyPosition, gameState)
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `black pawn should move 1 or 2 fields forward from initial position`() {
        val startingPos = Position(1, 1)


        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last()
        val blackPawnPosition = Position(1, 1)
        val validMoves = checkmateCore.getValidMoves(blackPawnPosition, gameState)

        val expectedMoves =
            listOf(
                Move(from = startingPos, to = startingPos - Position(1, 0)),
                Move(from = startingPos, to = startingPos - Position(2, 0))
            )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should move 1 or 2 fields forward from initial position`() {
        val startingPos = Position(6, 4)

        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last()
        val blackPawnPosition = startingPos
        val validMoves = checkmateCore.getValidMoves(blackPawnPosition, gameState)

        val expectedMoves =
            listOf(
                Move(from = startingPos, to = startingPos + Position(1, 0)),
                Move(from = startingPos, to = startingPos + Position(2, 0))
            )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white and black pawns should move in the correct direction from the middle of the field`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][2] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }
            )
        }

        val whitePawnPosition = Position(4, 2)
        val blackPawnPosition = Position(4, 4)

        val whitePawnMoves = checkmateCore.getValidMoves(whitePawnPosition, gameState)
        val blackPawnMoves = checkmateCore.getValidMoves(blackPawnPosition, gameState)

        val expectedWhitePawnMove = Move(from = whitePawnPosition, to = whitePawnPosition + Position(1, 0))
        val expectedBlackPawnMove = Move(from = blackPawnPosition, to = blackPawnPosition - Position(1, 0))

        assertTrue(whitePawnMoves.contains(expectedWhitePawnMove))
        assertTrue(blackPawnMoves.contains(expectedBlackPawnMove))
    }

    @Test
    fun `pawn should not move when blocked by another piece`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[1][1] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[2][1] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(1, 1), gameState)

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white pawn should be able to perform en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[6][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
                lastMove = Move(Position(6, 5), Position(4, 5))
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMove = Move(from = Position(4, 4), to = Position(5, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `black pawn should be able to perform en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[1][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
                lastMove = Move(Position(1, 4), Position(3, 4))
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(3, 3), gameState)

        val expectedMove = Move(from = Position(3, 3), to = Position(2, 4))
        assertTrue(validMoves.contains(expectedMove))
    }
}