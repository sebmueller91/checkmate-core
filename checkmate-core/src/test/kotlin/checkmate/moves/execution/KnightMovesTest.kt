package checkmate.moves.execution

import checkmate.compareTo
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.Test

class KnightMovesTest {
    @Test
    fun `white knight can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][4] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black knight can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][4] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white knight can capture black piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                    this[5][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 4), capture = Position(5, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][4] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black knight can capture white piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                    this[5][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 4), capture = Position(5, 4)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][4] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }
}