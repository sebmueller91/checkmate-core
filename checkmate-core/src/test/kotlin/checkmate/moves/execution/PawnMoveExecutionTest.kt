package checkmate.moves.execution

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
            currentPlayer = Player.BLACK,
            halfMoveClock = gameState.halfmoveClock+1,
            fullMoveNumber = gameState.fullmoveNumber,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[2][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.isEqualTo(result))
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
            currentPlayer = Player.WHITE,
            halfMoveClock = gameState.halfmoveClock+1,
            fullMoveNumber = gameState.fullmoveNumber+1,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.isEqualTo(result))
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
            currentPlayer = Player.BLACK,
            halfMoveClock = gameState.halfmoveClock+1,
            fullMoveNumber = gameState.fullmoveNumber,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[2][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.isEqualTo(result))
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
            currentPlayer = Player.WHITE,
            halfMoveClock = gameState.halfmoveClock+1,
            fullMoveNumber = gameState.fullmoveNumber+1,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.isEqualTo(result))
    }
}