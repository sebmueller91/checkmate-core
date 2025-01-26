package checkmate.moves.legal

import checkmate.CheckmateCoreImpl
import checkmate.exception.InvalidPositionException
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

internal class LegalMovesTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `getting moves from invalid position throws InvalidPositionException`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)

                })

        assertThrows<InvalidPositionException>() {
            checkmateCore.getValidMoves(gameState, Position(8, 3))
        }

    }

    @Test
    fun `getting moves from empty position throws InvalidPositionException`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)

                })

        val result = checkmateCore.getValidMoves(gameState, Position(4, 4))

        assertEquals(emptyList(), result)
    }

    @Test
    fun `white pawn should be able to perform legal move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)

                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(1, 3))
        assert(
            validMoves.contains(
                Move(
                    Position(1, 3),
                    Position(3, 3),
                )
            )
        )
    }

    @Test
    fun `black pawn should be able to perform legal move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[6][3] = Piece(type = Type.PAWN, color = Player.BLACK)

                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(6, 3))
        assert(
            validMoves.contains(
                Move(
                    Position(6, 3),
                    Position(4, 3),
                )
            )
        )
    }

    @Test
    fun `white bishop not should be able to perform move when king is in check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][7] = Piece(type = Type.BISHOP, color = Player.WHITE)
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][4] = Piece(type = Type.ROOK, color = Player.BLACK)
                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 7))
        assert(validMoves.isEmpty())
    }

    @Test
    fun `black bishop not should be able to perform move when king is in check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][7] = Piece(type = Type.BISHOP, color = Player.BLACK)
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[0][4] = Piece(type = Type.ROOK, color = Player.WHITE)
                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 7))
        assert(validMoves.isEmpty())
    }

    @Test
    fun `white rook should be able to perform move when king can be freed from check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 7))
        assert(
            validMoves.contains(
                Move(from = Position(7, 7), to = Position(0, 7), capture = Position(0, 7))
            )
        )
    }

    @Test
    fun `black rook should be able to perform move when king can be freed from check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[0][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                })

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 7))
        assert(
            validMoves.contains(
                Move(from = Position(7, 7), to = Position(0, 7), capture = Position(0, 7))
            )
        )
    }

    @Test
    fun `get valid moves returns moves of all pieces`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[4][4] = Piece(type = Type.KING, color = Player.BLACK)
                })

        val validMoves = checkmateCore.getValidMoves(gameState)
        assert(validMoves.size > 10)
    }
}