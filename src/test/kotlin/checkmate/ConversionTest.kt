package checkmate

import checkmate.model.*
import checkmate.moves.model.FILE_A
import checkmate.moves.model.RANK_3
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import toGameState

internal class ConversionTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `generateInitialState should return a chess board with initial setup`() {

        val result: Game = checkmateCore.generateInitialState()

        assert(result.gameStates.size == 1)
        val gameState = result.gameStates[0]

        assert(gameState.currentPlayer == Player.WHITE)
        assert(gameState.gameStatus == GameStatus.ONGOING)

        assert(gameState.board.flatten().filterNotNull().size == 32)
        assert(gameState.halfMoveClock == 0)
        assert(gameState.fullMoveNumber == 1)
        assert(gameState.castlingRights  == CastlingRights(true, true, true, true))

        val board = gameState.board
        assert(board[0][0]?.type == Type.ROOK && board[0][0]?.color == Player.WHITE)
        assert(board[0][1]?.type == Type.KNIGHT && board[0][1]?.color == Player.WHITE)
        assert(board[0][2]?.type == Type.BISHOP && board[0][2]?.color == Player.WHITE)
        assert(board[0][3]?.type == Type.QUEEN && board[0][3]?.color == Player.WHITE)
        assert(board[0][4]?.type == Type.KING && board[0][4]?.color == Player.WHITE)
        assert(board[0][5]?.type == Type.BISHOP && board[0][5]?.color == Player.WHITE)
        assert(board[0][6]?.type == Type.KNIGHT && board[0][6]?.color == Player.WHITE)
        assert(board[0][7]?.type == Type.ROOK && board[0][7]?.color == Player.WHITE)
        for (i in 0..7) {
            assert(board[1][i]?.type == Type.PAWN && board[1][i]?.color == Player.WHITE)
        }

        for (i in 0..7) {
            assert(board[6][i]?.type == Type.PAWN && board[6][i]?.color == Player.BLACK)
        }
        assert(board[7][0]?.type == Type.ROOK && board[7][0]?.color == Player.BLACK)
        assert(board[7][1]?.type == Type.KNIGHT && board[7][1]?.color == Player.BLACK)
        assert(board[7][2]?.type == Type.BISHOP && board[7][2]?.color == Player.BLACK)
        assert(board[7][3]?.type == Type.QUEEN && board[7][3]?.color == Player.BLACK)
        assert(board[7][4]?.type == Type.KING && board[7][4]?.color == Player.BLACK)
        assert(board[7][5]?.type == Type.BISHOP && board[7][5]?.color == Player.BLACK)
        assert(board[7][6]?.type == Type.KNIGHT && board[7][6]?.color == Player.BLACK)
        assert(board[7][7]?.type == Type.ROOK && board[7][7]?.color == Player.BLACK)
    }

    @Test
    fun `transformation from initial GameState to BitmapGameState and back to GameState should be correct`() {
        val initialState: GameState = checkmateCore.generateInitialState().gameStates[0]
        val bitmapGameState = initialState.toBitmapGameState()
        val transformedGameState = bitmapGameState.toGameState(null)

        assert(initialState == transformedGameState)
    }

    @Test
    fun `transformation from mofified GameState to BitmapGameState and back to GameState should be correct`() {
        val initialState = checkmateCore.generateInitialState().gameStates[0]
        val modifiedState: GameState = initialState.copy(
            currentPlayer = Player.BLACK,
            castlingRights = CastlingRights(false, true, false, false),
            halfMoveClock = 102,
            fullMoveNumber = 24,
            lastMove = Move(from = Position(1, 0), to = Position(3, 0)),
            board = initialState.board.map { it.toMutableList() }.toMutableList().apply {
                this[1][0] = null
                this[3][0] = Piece(type = Type.PAWN, color = Player.WHITE)
            },
        )
        val bitmapGameState = modifiedState.toBitmapGameState()
        val transformedGameState = bitmapGameState.toGameState(modifiedState.lastMove)

        assert(bitmapGameState.enPassantTarget == (RANK_3 and FILE_A))
        assert(modifiedState == transformedGameState)
    }

    @Test
    fun `transformation from mofified GameState to BitmapGameState and back to GameState should be correct with no en passant target`() {
        val initialState = checkmateCore.generateInitialState().gameStates[0]
        val modifiedState: GameState = initialState.copy(
            currentPlayer = Player.BLACK,
            castlingRights = CastlingRights(false, true, false, false),
            halfMoveClock = 102,
            fullMoveNumber = 24,
            lastMove = Move(from = Position(2, 0), to = Position(4, 0)),
            board = initialState.board.map { it.toMutableList() }.toMutableList().apply {
                this[2][0] = null
                this[3][0] = Piece(type = Type.PAWN, color = Player.WHITE)
            },
        )
        val bitmapGameState = modifiedState.toBitmapGameState()
        val transformedGameState = bitmapGameState.toGameState(modifiedState.lastMove)

        assert(bitmapGameState.enPassantTarget == 0UL)
        assert(modifiedState == transformedGameState)
    }
}