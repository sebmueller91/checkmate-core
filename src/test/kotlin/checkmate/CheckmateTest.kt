package checkmate

import checkmate.model.Piece
import checkmate.model.Player
import checkmate.model.Type
import checkmate.moves.isCheckmate
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CheckmateTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `white is not checkmate in initial setup`() {
        val initialState = checkmateCore.getInitialGame().gameStates.last()
        val isCheckmate = isCheckmate(initialState.toBitmapGameState())
        assert(!isCheckmate)
    }

    @Test
    fun `black is not checkmate in initial setup`() {
        val initialState = checkmateCore.getInitialGame().gameStates.last()
        val isCheckmate = isCheckmate(initialState.toBitmapGameState())
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns true if white is checkmate`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[2][2] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }).toBitmapGameState()

        val isCheckmate = isCheckmate(gameState)
        assert(isCheckmate)
    }

    @Test
    fun `checkmate returns true if black is checkmate`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[2][2] = Piece(type = Type.QUEEN, color = Player.WHITE)
                })

        val isCheckmate = isCheckmate(gameState.toBitmapGameState())
        assert(isCheckmate)
    }

    @Test
    fun `checkmate returns false if white king is not in check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if black king is not in check`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if white can free itself from check by moving king`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }).toBitmapGameState()

        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if black can free itself from check by moving king`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if white can free itself by capturing`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.BLACK)
                    this[4][6] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if black can free itself by capturing`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.WHITE)
                    this[4][6] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if white can free itself by blocking path`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.BLACK)
                    this[1][6] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()


        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }

    @Test
    fun `checkmate returns false if black can free itself by blocking path`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[4][4] = Piece(type = Type.QUEEN, color = Player.WHITE)
                    this[1][6] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val isCheckmate = isCheckmate(gameState)
        assert(!isCheckmate)
    }
}

