package checkmate.moves.pseudolegal

import checkmate.CheckmateCoreImpl
import checkmate.model.*
import checkmate.moves.type.BishopMoves
import checkmate.moves.type.PawnMoves
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class WhitePawnPseudoLegalMovesTest {
    private lateinit var checkmateCore: CheckmateCoreImpl

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCoreImpl()
    }

    @Test
    fun `getValidMoves should return an empty list for an empty field`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().toBitmapGameState()
        val emptyPosition = Position(4, 4)
        val validMoves = BishopMoves.generatePseudoLegalMoves(gameState, emptyPosition)
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white pawn should move 1 or 2 fields forward from initial position`() {
        val startingPos = Position(1, 4)

        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().copy(currentPlayer = Player.WHITE).toBitmapGameState()
        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, startingPos)
        val expectedMoves = listOf(
            Move(from = startingPos, to = startingPos + Position(1, 0)),
            Move(from = startingPos, to = startingPos + Position(2, 0))
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should move 1 field forward from the middle of the board`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][2] = Piece(type = Type.PAWN, color = Player.WHITE)
                }
            )
        }.toBitmapGameState()

        val whitePawnPosition = Position(4, 2)
        val whitePawnMoves = PawnMoves.generatePseudoLegalMoves(gameState, whitePawnPosition)
        val expectedWhitePawnMove = Move(from = whitePawnPosition, to = whitePawnPosition + Position(1, 0))

        assertTrue(whitePawnMoves.contains(expectedWhitePawnMove))
    }

    @Test
    fun `white pawn should not move when blocked by another piece`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList()
                    .apply {
                        this[1][1] = Piece(type = Type.PAWN, color = Player.BLACK)
                        this[2][1] = Piece(type = Type.PAWN, color = Player.WHITE)
                    })
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(1,1))
        assertTrue(validMoves.isEmpty())
    }

    @Test
    fun `white pawn should capture diagonally to the left`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(5, 3), capture = Position(5, 3))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should capture diagonally to the right`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][5] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(5, 5), capture = Position(5, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should be able to perform left en-passant`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[6][3] = null
                },
                lastMove = Move(Position(6, 3), Position(4, 3))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(5, 3), capture = Position(4, 3))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should be able to perform right en-passant`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][5] = Piece(type = Type.PAWN, color = Player.BLACK)
                    this[6][5] = null
                },
                lastMove = Move(Position(6, 5), Position(4, 5))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        val expectedMove = Move(from = Position(4, 4), to = Position(5, 5), capture = Position(4, 5))
        assertTrue(validMoves.contains(expectedMove))
    }

    @Test
    fun `white pawn should promote when reaching the last row`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[6][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[7][4] = null
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(6,4))
        val expectedMoves = listOf(
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.QUEEN
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.ROOK
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.KNIGHT
            ),
            Move(
                from = Position(6, 4),
                to = Position(7, 4),
                promotion = Type.BISHOP
            )
        )
        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should promote when capturing on the last row`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[6][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[7][5] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(6,4))
        val expectedMoves = listOf(
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.QUEEN),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.ROOK),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.KNIGHT),
            Move(from = Position(6, 4), to = Position(7, 5), capture = Position(7, 5), promotion = Type.BISHOP)
        )

        assertTrue(validMoves.containsAll(expectedMoves))
    }

    @Test
    fun `white pawn should not promote if not on the last row`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[5][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[6][3] = null
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(5,3))
        assertTrue(validMoves.none { it.promotion != null })
    }

    @Test
    fun `white pawn should capture en-passant only immediately after the opponent's double move`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][4] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
                lastMove = null
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,4))
        assertTrue(validMoves.none { it.to == Position(5, 3) })
    }

    @Test
    fun `white pawn should not capture from left edge to right edge of the board`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][0] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][7] = Piece(type = Type.ROOK, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,0))
        assertTrue(validMoves.none { it.to == Position(5, 7) })
    }

    @Test
    fun `white pawn should not capture from right edge to left edge of the board`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][7] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[5][0] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,7))
        assertTrue(validMoves.none { it.to == Position(5, 0) })
    }

    @Test
    fun `white pawn should not perform left en-passant from left edge to right edge`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][0] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][7] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
                lastMove = Move(Position(6, 7), Position(4, 7))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,0))
        assertTrue(validMoves.none { it.to == Position(5, 7) })
    }

    @Test
    fun `white pawn should not perform right en-passant from right edge to left edge`() {
        val game = checkmateCore.getInitialGame()
        val gameState = game.gameStates.last().let { state ->
            state.copy(
                board = state.board.map { it.toMutableList() }.toMutableList().apply {
                    this[4][7] = Piece(type = Type.PAWN, color = Player.WHITE)
                    this[4][0] = Piece(type = Type.PAWN, color = Player.BLACK)
                },
                lastMove = Move(Position(6, 0), Position(4, 0))
            )
        }.toBitmapGameState()

        val validMoves = PawnMoves.generatePseudoLegalMoves(gameState, Position(4,7))
        assertTrue(validMoves.none { it.to == Position(5, 0) })
    }
}