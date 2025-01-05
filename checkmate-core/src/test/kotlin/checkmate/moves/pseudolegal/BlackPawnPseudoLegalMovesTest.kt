package checkmate.moves.pseudolegal

import checkmate.CheckmateCore
import checkmate.model.*
import checkmate.moves.PawnMoves
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class BlackPawnPseudoLegalMovesTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `getValidMoves should return an empty list for an empty field`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().toBitmapGameState()
        val emptyPosition = Position(4, 4)
        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, emptyPosition)
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `black pawn should move 1 or 2 fields forward from initial position`() {
        val startingPos = Position(6, 4)

        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().copy(currentPlayer = Player.BLACK).toBitmapGameState()
        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, startingPos)
        val expectedMoves = listOf(
            Move(from = startingPos, to = startingPos + Position(-1, 0)),
            Move(from = startingPos, to = startingPos + Position(-2, 0))
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `black pawn should move 1 field forward from the middle of the board`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][2] = Piece(type = Type.PAWN, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val blackPawnPosition = Position(4, 2)
        val blackPawnMoves = PawnMoves.generatePseudoLegalMoves(gameState, blackPawnPosition)

        val expectedBlackPawnMove = Move(from = blackPawnPosition, to = blackPawnPosition + Position(-1, 0))

        assertTrue(blackPawnMoves.contains(expectedBlackPawnMove))
    }

    @Test
    fun `black pawn should not move when blocked by another piece`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[5][1] = Piece(type = Type.PAWN, color = Player.WHITE)
                        this[4][1] = Piece(type = Type.PAWN, color = Player.BLACK)
                    })
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(5,1))

        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `black pawn should capture diagonally to the left`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(3, 3), capture = Position(3, 3))

        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `black pawn should capture diagonally to the right`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][5] = Piece(type = Type.ROOK, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(3, 5), capture = Position(3, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `black pawn should be able to perform left en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[1][3] = null
                },
                lastMove = Move(Position(1, 3), Position(3, 3))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(3,4))
        val expectedMove = Move(from = Position(3, 4), to = Position(2, 3), capture = Position(3, 3))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `black pawn should be able to perform right en-passant`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[1][5] = null
                },
                lastMove = Move(Position(1, 5), Position(3, 5))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(3,4))
        val expectedMove = Move(from = Position(3, 4), to = Position(2, 5), capture = Position(3, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `black pawn should promote when reaching the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[1][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[0][4] = null
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(1,4))
        val expectedMoves = listOf(
            Move(
                from = Position(1, 4),
                to = Position(0, 4),
                promotion = Type.QUEEN
            ),
            Move(
                from = Position(1, 4),
                to = Position(0, 4),
                promotion = Type.ROOK
            ),
            Move(
                from = Position(1, 4),
                to = Position(0, 4),
                promotion = Type.KNIGHT
            ),
            Move(
                from = Position(1, 4),
                to = Position(0, 4),
                promotion = Type.BISHOP
            )
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `black pawn should promote when capturing on the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[1][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[0][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(1,4))
        val expectedMoves = listOf(
            Move(from = Position(1, 4), to = Position(0, 3), capture = Position(0, 3), promotion = Type.QUEEN),
            Move(from = Position(1, 4), to = Position(0, 3), capture = Position(0, 3), promotion = Type.ROOK),
            Move(from = Position(1, 4), to = Position(0, 3), capture = Position(0, 3), promotion = Type.KNIGHT),
            Move(from = Position(1, 4), to = Position(0, 3), capture = Position(0, 3), promotion = Type.BISHOP)
        )

        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `black pawn should not promote if not on the last row`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[2][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[1][3] = null
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(2,3))
        assertTrue(validMoves.none { it.promotion != null })
    }

    @Test
    fun `black pawn should capture en-passant only immediately after the opponent's double move`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[3][4] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][5] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
                lastMove = null
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(3,4))
        assertTrue(validMoves.none { it.to == Position(2, 5) })
    }

    @Test
    fun `black pawn should not capture from left edge to right edge of the board`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][0] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][7] = Piece(type = Type.ROOK, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,0))
        assertTrue(validMoves.none { it.to == Position(3, 7) })
    }

    @Test
    fun `black pawn should not capture from right edge to left edge of the board`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[3][0] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,7))
        assertTrue(validMoves.none { it.to == Position(3, 0) })
    }

    @Test
    fun `black pawn should not perform left en-passant from left edge to right edge`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][0] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[4][7] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
                lastMove = Move(Position(6, 7), Position(4, 7))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,0))
        assertTrue(validMoves.none { it.to == Position(3, 7) })
    }

    @Test
    fun `black pawn should not perform right en-passant from right edge to left edge`() {
        val game = checkmateCore.generateInitialState()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                currentPlayer = Player.BLACK,
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[4][0] = Piece(type = Type.PAWN, color = Player.WHITE)
                },
                lastMove = Move(Position(6, 0), Position(4, 0))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,7))
        assertTrue(validMoves.none { it.to == Position(3, 0) })
    }
}