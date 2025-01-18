package checkmate.moves.execution

import checkmate.compareTo
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.Test

internal class PawnMoveExecutionTest {
    @Test
    fun `white pawn can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(2, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[2][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black pawn can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(4, 3), to = Position(3, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white pawn can capture`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[2][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(2, 4), capture = Position(2, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[2][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black pawn can capture`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(4, 3), to = Position(3, 4), capture = Position(3, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white pawn double move sets en passant target`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
        ).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 4), to = Position(3, 4)))

        val expectedGameState = emptyGameState.copy(
            lastMove = Move(from = Position(1, 4), to = Position(3, 4)),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black pawn double move sets en passant target`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[6][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
        ).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(6, 4), to = Position(4, 4)))

        val expectedGameState = emptyGameState.copy(
            lastMove = Move(from = Position(6, 4), to = Position(4, 4)),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white pawn can capture en passant`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
            lastMove = Move(from = Position(6, 3), to = Position(4, 3))
        ).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(4, 4), to = Position(5, 3), capture = Position(4, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black pawn can capture en passant`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[1][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
            lastMove = Move(from = Position(1, 4), to = Position(3, 4))
        ).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(2, 4), capture = Position(1, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[2][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }
}