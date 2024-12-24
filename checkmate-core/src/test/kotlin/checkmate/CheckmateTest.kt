package checkmate

import checkmate.impl.CheckmateCoreImpl
import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CheckmateTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `generateInitialState should return a chess board with initial setup`() {

        val result: GameState = checkmateCore.generateInitialState()

        assert(result.currentPlayer == Player.WHITE)
        assert(result.moveHistory.isEmpty())
        assert(result.gameStatus == GameStatus.ONGOING)

        assert(result.board.flatten().size == 16)
        assert(result.board.flatten().filterNotNull().size == 16)
        assert(result.board.flatten().none { !it!!.isMoved })

        val board = result.board
        assert(board[0][0]?.type == Type.ROOK && board[0][0]?.color == Player.BLACK)
        assert(board[0][1]?.type == Type.KNIGHT && board[0][1]?.color == Player.BLACK)
        assert(board[0][2]?.type == Type.BISHOP && board[0][2]?.color == Player.BLACK)
        assert(board[0][3]?.type == Type.QUEEN && board[0][3]?.color == Player.BLACK)
        assert(board[0][4]?.type == Type.KING && board[0][4]?.color == Player.BLACK)
        assert(board[0][5]?.type == Type.BISHOP && board[0][5]?.color == Player.BLACK)
        assert(board[0][6]?.type == Type.KNIGHT && board[0][6]?.color == Player.BLACK)
        assert(board[0][7]?.type == Type.ROOK && board[0][7]?.color == Player.BLACK)
        for (i in 0..7) {
            assert(board[1][i]?.type == Type.PAWN && board[1][i]?.color == Player.BLACK)
        }

        for (i in 0..7) {
            assert(board[6][i]?.type == Type.PAWN && board[6][i]?.color == Player.WHITE)
        }
        assert(board[7][0]?.type == Type.ROOK && board[7][0]?.color == Player.WHITE)
        assert(board[7][1]?.type == Type.KNIGHT && board[7][1]?.color == Player.WHITE)
        assert(board[7][2]?.type == Type.BISHOP && board[7][2]?.color == Player.WHITE)
        assert(board[7][3]?.type == Type.QUEEN && board[7][3]?.color == Player.WHITE)
        assert(board[7][4]?.type == Type.KING && board[7][4]?.color == Player.WHITE)
        assert(board[7][5]?.type == Type.BISHOP && board[7][5]?.color == Player.WHITE)
        assert(board[7][6]?.type == Type.KNIGHT && board[7][6]?.color == Player.WHITE)
        assert(board[7][7]?.type == Type.ROOK && board[7][7]?.color == Player.WHITE)
    }
}