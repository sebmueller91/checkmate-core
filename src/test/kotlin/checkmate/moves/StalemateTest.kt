package checkmate.moves

import checkmate.CheckmateCoreImpl
import checkmate.generateEmptyBoardGameState
import checkmate.model.CastlingRights
import checkmate.model.Piece
import checkmate.model.Player
import checkmate.model.Type
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StalemateTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `white is not stalemate in initial setup`() {
        val initialState = checkmateCore.generateInitialState().gameStates.last()
        val isStalemate = isStalemate(initialState.toBitmapGameState())
        assert(isStalemate.not())
    }

    @Test
    fun `black is not stalemate in initial setup`() {
        val initialState = checkmateCore.generateInitialState().gameStates.last()
        val isStalemate = isStalemate(initialState.toBitmapGameState())
        assert(isStalemate.not())
    }

    @Test
    fun `stalemate returns false if white is checkmate`() {
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

        val isStalemate = isStalemate(gameState)
        assert(isStalemate.not())
    }

    @Test
    fun `stalemate returns false if black is checkmate`() {
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

        val isStalemate = isStalemate(gameState.toBitmapGameState())
        assert(isStalemate.not())
    }

    @Test
    fun `stalemate returns true if white is stalemate`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            castlingRights = CastlingRights(
                whiteQueenSide = false,
                whiteKingSide = false,
                blackQueenSide = false,
                blackKingSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val isStalemate = isStalemate(gameState)
        assert(isStalemate)
    }

    @Test
    fun `stalemate returns true if black is stalemate`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            castlingRights = CastlingRights(
                whiteQueenSide = false,
                whiteKingSide = false,
                blackQueenSide = false,
                blackKingSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val isStalemate = isStalemate(gameState)
        assert(isStalemate)
    }

    @Test
    fun `stalemate returns false if white can move piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            castlingRights = CastlingRights(
                whiteQueenSide = false,
                whiteKingSide = false,
                blackQueenSide = false,
                blackKingSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.WHITE)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }).toBitmapGameState()

        val isStalemate = isStalemate(gameState)
        assert(isStalemate.not())
    }

    @Test
    fun `stalemate returns false if black can move piece`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            castlingRights = CastlingRights(
                whiteQueenSide = false,
                whiteKingSide = false,
                blackQueenSide = false,
                blackKingSide = false
            ),
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = Piece(type = Type.KING, color = Player.BLACK)
                    this[7][1] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[1][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }).toBitmapGameState()

        val isStalemate = isStalemate(gameState)
        assert(isStalemate.not())
    }
}