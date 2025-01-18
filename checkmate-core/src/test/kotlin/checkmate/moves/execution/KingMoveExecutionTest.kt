package checkmate.moves.execution

import checkmate.compareTo
import checkmate.generateEmptyBoardGameState
import checkmate.model.*
import checkmate.moves.executeMove
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.Test

internal class KingMoveExecutionTest {
    @Test
    fun `white king can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KING, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.KING, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black king can perform simple move`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,

            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KING, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.KING, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white king can capture black piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KING, color = Player.WHITE)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5), capture = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.KING, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black king can capture white piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KING, color = Player.BLACK)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(Move(from = Position(3, 3), to = Position(5, 5), capture = Position(5, 5)))

        val expectedGameState = emptyGameState.copy(
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[5][5] = Piece(type = Type.KING, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white king can castle queen side`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][0] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(
            Move(
                from = Position(0, 4),
                to = Position(0, 2),
                castlingRookFromTo = Pair(Position(0, 0), Position(0, 3))
            )
        )

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteKingSide = false,
                whiteQueenSide = false,
                blackKingSide = true,
                blackQueenSide = true
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][2] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `white king can castle king side`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val result = gameState.executeMove(
            Move(
                from = Position(0, 4),
                to = Position(0, 6),
                castlingRookFromTo = Pair(Position(0, 7), Position(0, 5))
            )
        )

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteKingSide = false,
                whiteQueenSide = false,
                blackKingSide = true,
                blackQueenSide = true
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][6] = Piece(type = Type.KING, color = Player.WHITE)
                    this[0][5] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black king can castle queen side`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][0] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(
            Move(
                from = Position(7, 4),
                to = Position(7, 2),
                castlingRookFromTo = Pair(Position(7, 0), Position(7, 3))
            )
        )

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteKingSide = true,
                whiteQueenSide = true,
                blackKingSide = false,
                blackQueenSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][2] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }

    @Test
    fun `black king can castle king side`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val result = gameState.executeMove(
            Move(
                from = Position(7, 4),
                to = Position(7, 6),
                castlingRookFromTo = Pair(Position(7, 7), Position(7, 5))
            )
        )

        val expectedGameState = emptyGameState.copy(
            castlingRights = CastlingRights(
                whiteKingSide = true,
                whiteQueenSide = true,
                blackKingSide = false,
                blackQueenSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[7][6] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][5] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        assert(expectedGameState.compareTo(result))
    }
}