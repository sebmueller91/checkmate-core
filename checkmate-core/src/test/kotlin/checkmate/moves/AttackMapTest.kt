package checkmate.moves

import checkmate.CheckmateCore
import checkmate.generateEmptyBoardGameState
import checkmate.model.Piece
import checkmate.model.Player
import checkmate.model.Position
import checkmate.model.Type
import checkmate.moves.type.*
import checkmate.util.printAsBoard
import checkmate.util.toBitmapGameState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class AttackMapTest {
    private lateinit var checkmateCore: CheckmateCore

    @BeforeEach
    fun setUp() {
        checkmateCore = CheckmateCore()
    }

    @Test
    fun `white pawn has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[1][3] = Piece(type = Type.PAWN, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = PawnMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(listOf(Position(2, 2), Position(2, 4)))
        expectedAttackMap.printAsBoard("Expected attack map")
        attackMap.printAsBoard("Attack map")
        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `black pawn has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[6][3] = Piece(type = Type.PAWN, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = PawnMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(listOf(Position(5, 2), Position(5, 4)))
        expectedAttackMap.printAsBoard("Expected attack map")
        attackMap.printAsBoard("Attack map")
        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `white rook has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.ROOK, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = RookMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(3, 0), Position(3, 1), Position(3, 2), Position(3, 4), Position(3, 5),
                Position(3, 6), Position(3, 7), Position(0, 3), Position(1, 3), Position(2, 3),
                Position(4, 3), Position(5, 3), Position(6, 3), Position(7, 3)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `black rook has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.ROOK, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = RookMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(3, 0), Position(3, 1), Position(3, 2), Position(3, 4), Position(3, 5),
                Position(3, 6), Position(3, 7), Position(0, 3), Position(1, 3), Position(2, 3),
                Position(4, 3), Position(5, 3), Position(6, 3), Position(7, 3)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `white knight has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = KnightMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(1, 2), Position(1, 4), Position(2, 1), Position(2, 5),
                Position(4, 1), Position(4, 5), Position(5, 2), Position(5, 4)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `black knight has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.KNIGHT, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = KnightMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(1, 2), Position(1, 4), Position(2, 1), Position(2, 5),
                Position(4, 1), Position(4, 5), Position(5, 2), Position(5, 4)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `white bishop has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = BishopMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(0, 0), Position(1, 1), Position(2, 2), Position(4, 4),
                Position(5, 5), Position(6, 6), Position(7, 7), Position(0, 6),
                Position(1, 5), Position(2, 4), Position(4, 2), Position(5, 1),
                Position(6, 0)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `black bishop has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.BISHOP, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = BishopMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(0, 0), Position(1, 1), Position(2, 2), Position(4, 4),
                Position(5, 5), Position(6, 6), Position(7, 7), Position(0, 6),
                Position(1, 5), Position(2, 4), Position(4, 2), Position(5, 1),
                Position(6, 0)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `white queen has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = QueenMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(0, 0), Position(1, 1), Position(2, 2), Position(4, 4),
                Position(5, 5), Position(6, 6), Position(7, 7), Position(0, 6),
                Position(1, 5), Position(2, 4), Position(4, 2), Position(5, 1),
                Position(6, 0), Position(3, 0), Position(3, 1), Position(3, 2),
                Position(3, 4), Position(3, 5), Position(3, 6), Position(3, 7),
                Position(0, 3), Position(1, 3), Position(2, 3), Position(4, 3),
                Position(5, 3), Position(6, 3), Position(7, 3)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `black queen has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = QueenMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(0, 0), Position(1, 1), Position(2, 2), Position(4, 4),
                Position(5, 5), Position(6, 6), Position(7, 7), Position(0, 6),
                Position(1, 5), Position(2, 4), Position(4, 2), Position(5, 1),
                Position(6, 0), Position(3, 0), Position(3, 1), Position(3, 2),
                Position(3, 4), Position(3, 5), Position(3, 6), Position(3, 7),
                Position(0, 3), Position(1, 3), Position(2, 3), Position(4, 3),
                Position(5, 3), Position(6, 3), Position(7, 3)
            )
        )

        assertEquals(expectedAttackMap, attackMap)
    }

    @Test
    fun `white king has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.BLACK,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.WHITE)
                }).toBitmapGameState()

        val attackMap = KnightMoves.generateAttackMap(gameState, Player.WHITE)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(2, 2), Position(2, 4), Position(4, 2), Position(4, 4),
                Position(2, 3), Position(4, 3), Position(3, 2), Position(3, 4)
            )
        )
    }

    @Test
    fun `black king has correct attack map`() {
        val emptyGameState = generateEmptyBoardGameState()
        val gameState = emptyGameState.copy(
            currentPlayer = Player.WHITE,
            board = emptyGameState.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[3][3] = Piece(type = Type.QUEEN, color = Player.BLACK)
                }).toBitmapGameState()

        val attackMap = KnightMoves.generateAttackMap(gameState, Player.BLACK)
        val expectedAttackMap = generateBitmapFromPositions(
            listOf(
                Position(2, 2), Position(2, 4), Position(4, 2), Position(4, 4),
                Position(2, 3), Position(4, 3), Position(3, 2), Position(3, 4)
            )
        )
    }

    private fun generateBitmapFromPositions(attackedCells: List<Position>): ULong {
        var bitmap: ULong = 0u
        for ((row, col) in attackedCells) {
            val position = row * 8 + col
            bitmap = bitmap or (1uL shl position)
        }
        return bitmap
    }
}