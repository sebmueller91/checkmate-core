package checkmate.moves.legal

import checkmate.CheckmateCore
import checkmate.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class KingMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `white king should be able to perform queen side castling`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][2] = null
                    this[0][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(
            validMoves.contains(
                Move(
                    Position(0, 4),
                    Position(0, 2),
                    castlingRookFromTo = Pair(Position(0, 0), Position(0, 3))
                )
            )
        )
    }

    @Test
    fun `white king should not be able to perform queen side castling when its not possible`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(whiteQueenSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][2] = null
                    this[0][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `white king should not be able to perform queen side castling when piece in between`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(whiteQueenSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `white king should not be able to perform queen side castling when king is in check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][2] = null
                    this[0][3] = null
                    this[2][5] = Piece(Type.KNIGHT, Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `white king should not be able to perform queen side castling when king needs to move through check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][2] = null
                    this[0][3] = null
                    this[2][4] = Piece(Type.KNIGHT, Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `white king should be able to perform king side castling`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][5] = null
                    this[0][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(
            validMoves.contains(
                Move(
                    Position(0, 4),
                    Position(0, 6),
                    castlingRookFromTo = Pair(Position(0, 7), Position(0, 5))
                )
            )
        )
    }

    @Test
    fun `white king should not be able to perform king side castling when its not possible`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(whiteKingSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][5] = null
                    this[0][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 6))))
    }

    @Test
    fun `white king should not be able to perform king side castling when piece in between`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 6))))
    }

    @Test
    fun `white king should not be able to perform king side castling when king is in check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][5] = null
                    this[0][6] = null
                    this[2][3] = Piece(Type.KNIGHT, Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `white king should not be able to perform king side castling when king needs to move through check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.WHITE,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[0][1] = null
                    this[0][3] = null
                    this[2][4] = Piece(Type.KNIGHT, Player.BLACK)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(0, 4))
        assert(!validMoves.contains(Move(Position(0, 4), Position(0, 2))))
    }

    @Test
    fun `black king should be able to perform queen side castling`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][2] = null
                    this[7][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(
            validMoves.contains(
                Move(
                    Position(7, 4),
                    Position(7, 2),
                    castlingRookFromTo = Pair(Position(7, 0), Position(7, 3))
                )
            )
        )
    }

    @Test
    fun `black king should not be able to perform queen side castling when its not possible`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                castlingRights = state.castlingRights.copy(blackQueenSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][2] = null
                    this[7][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }

    @Test
    fun `black king should not be able to perform queen side castling when piece in between`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                castlingRights = state.castlingRights.copy(blackQueenSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][3] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }

    @Test
    fun `black king should not be able to perform queen side castling when king is in check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][2] = null
                    this[7][3] = null
                    this[5][5] = Piece(Type.KNIGHT, Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }

    @Test
    fun `black king should not be able to perform queen side castling when king needs to move through check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][2] = null
                    this[7][3] = null
                    this[5][4] = Piece(Type.KNIGHT, Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }

    @Test
    fun `black king should be able to perform king side castling`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][5] = null
                    this[7][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))

        assert(
            validMoves.contains(
                Move(
                    Position(7, 4),
                    Position(7, 6),
                    castlingRookFromTo = Pair(Position(7, 7), Position(7, 5))
                )
            )
        )
    }

    @Test
    fun `black king should not be able to perform king side castling when its not possible`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                castlingRights = state.castlingRights.copy(blackKingSide = false),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][5] = null
                    this[7][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 6))))
    }

    @Test
    fun `black king should not be able to perform king side castling when piece in between`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][6] = null
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 6))))
    }

    @Test
    fun `black king should not be able to perform king side castling when king is in check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][5] = null
                    this[7][6] = null
                    this[5][3] = Piece(Type.KNIGHT, Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }

    @Test
    fun `black king should not be able to perform king side castling when king needs to move through check`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                castlingRights = state.castlingRights.copy(),
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[7][1] = null
                    this[7][3] = null
                    this[5][4] = Piece(Type.KNIGHT, Player.WHITE)
                }
            )
        }

        val validMoves = checkmateCore.getValidMoves(gameState, Position(7, 4))
        assert(!validMoves.contains(Move(Position(7, 4), Position(7, 2))))
    }
}