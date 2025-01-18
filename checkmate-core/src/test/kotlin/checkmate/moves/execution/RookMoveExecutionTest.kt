package checkmate.moves.execution

import checkmate.compareTo
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.Test

class RookMoveExecutionTest {
    @Test
    fun `white rook can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(5, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black rook can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(5, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white rook can capture black piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[5][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(5, 3), capture = Position(5, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black rook can capture white piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[5][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(1, 3), to = Position(5, 3), capture = Position(5, 3)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white left rook move clears queen side castling rights`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(0, 0), to = Position(0, 3)))

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteQueenSide = false,
                whiteKingSide = true,
                blackQueenSide = true,
                blackKingSide = true
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white right rook move clears king side castling rights`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(0, 7), to = Position(3, 7)))

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteQueenSide = true,
                whiteKingSide = false,
                blackQueenSide = true,
                blackKingSide = true
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black left rook move clears queen side castling rights`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][0] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(7, 0), to = Position(7, 3)))

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteQueenSide = true,
                whiteKingSide = true,
                blackQueenSide = false,
                blackKingSide = true
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black right rook move clears king side castling rights`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(7, 7), to = Position(7, 3)))

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteQueenSide = true,
                whiteKingSide = true,
                blackQueenSide = true,
                blackKingSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }
}