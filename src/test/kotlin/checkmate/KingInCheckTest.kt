package checkmate

import checkmate.model.Piece
import checkmate.model.Player
import checkmate.model.Type
import checkmate.moves.isKingInCheck
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KingInCheckTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `white is not in check in initial setup`() {
        val initialState = checkmateCore.getInitialGame().gameStates.last()
        val isKingInCheck = isKingInCheck(initialState.toBitmapGameState(), Player.WHITE)
        assert(!isKingInCheck)
    }

    @Test
    fun `black is not in check in initial setup`() {
        val initialState = checkmateCore.getInitialGame().gameStates.last()
        val isKingInCheck = isKingInCheck(initialState.toBitmapGameState(), Player.BLACK)
        assert(!isKingInCheck)
    }

    @Test
    fun `white can be in check by black pawn`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)

                }).toBitmapGameState()

        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `white can be in check by black rook`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[3][6] = Piece(type = Type.ROOK, color = Player.BLACK)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `white can be in check by black knight`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[1][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `white can be in check by black Bishop`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[5][6] = Piece(type = Type.BISHOP, color = Player.BLACK)

                }).toBitmapGameState()

        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `white can be in check by black queen`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[1][6] = Piece(type = Type.QUEEN, color = Player.BLACK)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `white can be in check by black king`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[3][5] = Piece(type = Type.KING, color = Player.BLACK)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white pawn`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white rook`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[3][6] = Piece(type = Type.ROOK, color = Player.WHITE)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white knight`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[1][3] = Piece(type = Type.KNIGHT, color = Player.WHITE)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white Bishop`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[5][6] = Piece(type = Type.BISHOP, color = Player.WHITE)

                }).toBitmapGameState()

        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white queen`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[1][6] = Piece(type = Type.QUEEN, color = Player.WHITE)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `black can be in check by white king`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[3][5] = Piece(type = Type.KING, color = Player.WHITE)

                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(isKingInCheck)
    }

    @Test
    fun `white not in check when path is blocked`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][4] = Piece(type = Type.KING, color = Player.WHITE)
                    this[4][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[7][7] = Piece(type = Type.QUEEN, color = Player.BLACK)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.WHITE)
        assert(!isKingInCheck)
    }

    @Test
    fun `black not in check when path is blocked`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[4][4] = Piece(type = Type.KING, color = Player.BLACK)
                    this[4][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[7][7] = Piece(type = Type.QUEEN, color = Player.WHITE)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[5][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()


        val isKingInCheck = isKingInCheck(gameState, Player.BLACK)
        assert(!isKingInCheck)
    }
}