package checkmate

import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WhitePawnMovesTest {
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
    fun `white pawn should move 1 or 2 fields forward from initial position`() {
        val startingPos = Position(1, 4)

        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().copy(currentPlayer = Player.WHITE)
        val whitePawnPosition = startingPos
        val validMoves = checkmateCore.getValidMoves(whitePawnPosition, gameState)

        val expectedMoves = listOf(
            Move(from = startingPos, to = startingPos + Position(1, 0)),
            Move(from = startingPos, to = startingPos + Position(2, 0))
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should move 1 field forward from the middle of the board`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][2] = Piece(type = Type.PAWN, color = Player.WHITE)
                }
            )
        }

        val whitePawnPosition = Position(4, 2)
        val whitePawnMoves = checkmateCore.getValidMoves(whitePawnPosition, gameState)

        val expectedWhitePawnMove = Move(from = whitePawnPosition, to = whitePawnPosition + Position(1, 0))

        assertTrue(whitePawnMoves.contains(expectedWhitePawnMove))
    }

    @Test
    fun `white pawn should not move when blocked by another piece`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[1][1] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[2][1] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }

        val validMoves = checkmateCore.getValidMoves(Position(1, 1), gameState)

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white pawn should capture diagonally to the left`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMove = Move(from = Position(4, 4), to = Position(5, 3), capture = Position(5, 3))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should capture diagonally to the right`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][5] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMove = Move(from = Position(4, 4), to = Position(5, 5), capture = Position(5, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should be able to perform left en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[6][3] = null
                },
                lastMove = Move(Position(6, 3), Position(4, 3))
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMove = Move(from = Position(4, 4), to = Position(5, 3), capture = Position(4, 3))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should be able to perform right en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[6][5] = null
                },
                lastMove = Move(Position(6, 5), Position(4, 5))
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(4, 4), gameState)

        val expectedMove = Move(from = Position(4, 4), to = Position(5, 5), capture = Position(4, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should promote when reaching the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[6][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[7][4] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(6, 4), gameState)

        val expectedMoves = listOf(
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.QUEEN
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.ROOK
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.KNIGHT
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.BISHOP
            )
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should promote when capturing on the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[6][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[7][5] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(6, 4), gameState)

        val expectedMoves = listOf(
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.QUEEN),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.ROOK),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.KNIGHT),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.BISHOP)
        )

        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should not promote if not on the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[5][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[6][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(Position(5, 3), gameState)

        assertTrue(validMoves.none { it.promotion != null })
    }
}