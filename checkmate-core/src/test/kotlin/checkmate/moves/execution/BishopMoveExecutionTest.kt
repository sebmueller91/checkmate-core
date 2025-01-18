package checkmate.moves.execution

import checkmate.compareTo
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.Test

class BishopMoveExecutionTest {
    @Test
    fun `white bishop can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black bishop can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white bishop can capture black piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.WHITE)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5), capture = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black bishop can capture white piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.BLACK)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5), capture = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }
}