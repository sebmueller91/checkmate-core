package checkmate.moves.type

import PrecomputedMovementMasks
import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.getPlayerAttackMap
import checkmate.moves.isLegalMove
import checkmate.moves.model.*
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object KingMoves : PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val king = if (gameState.isWhiteTurn) gameState.whiteKing else gameState.blackKing
        val opponents = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces

        val moves = mutableListOf<Move>()
        for (fromPos in extractPositions(king)) {
            val mask = PrecomputedMovementMasks.singleStepMasks[fromPos]

            val validMoves = mask and gameState.allPieces.inv()
            moves.addAll(extractPositions(validMoves).map { toPos ->
                createMove(fromPos, toPos, null)
            })

            val captures = mask and opponents
            moves.addAll(extractPositions(captures).map { toPos ->
                createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
            })
        }
        return moves
    }

    override fun generateLegalMoves(gameState: BitmapGameState): List<Move> {
        val legalMoves = generatePseudoLegalMoves(gameState).filter { isLegalMove(gameState, it) }.toMutableList()
        legalMoves.addAll(getCastlingMoves(gameState))
        return legalMoves
    }

    override fun generateAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val king = if (player == Player.WHITE) gameState.whiteKing else gameState.blackKing
        val opponents = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces

        var attackMask = 0UL
        for (fromPos in extractPositions(king)) {
            val mask = PrecomputedMovementMasks.singleStepMasks[fromPos]

            attackMask = attackMask or (mask and gameState.allPieces.inv())
            attackMask = attackMask or (mask and opponents)
        }
        return attackMask
    }

    private fun getCastlingMoves(gameState: BitmapGameState): List<Move> {
        val moves = mutableListOf<Move>()
        if (gameState.isWhiteTurn) {
            moves.addAll(whiteQueenSideCastling(gameState))
            moves.addAll(whiteKingSideCastling(gameState))
        } else {
            moves.addAll(blackQueenSideCastling(gameState))
            moves.addAll(blackKingSideCastling(gameState))
        }
        return moves
    }

    private fun whiteQueenSideCastling(gameState: BitmapGameState): List<Move> {
        if (gameState.castlingRights and WHITE_QUEEN_SIDE_CASTLING == 0) {
            return emptyList()
        }
        val attackMap = getPlayerAttackMap(gameState, Player.BLACK)
        val occupied = gameState.allPieces
        val kingPos = extractPositions(gameState.whiteKing).first()
        val requiredEmptyPositions = 0b111UL shl 1
        val requiredNotInCheckPositions = 0b111UL shl 2
        if (requiredEmptyPositions and occupied != 0UL) {
            return emptyList()
        }
        if (requiredNotInCheckPositions and attackMap != 0UL) {
            return emptyList()
        }
        return listOf(createMove(fromPos = kingPos, toPos = kingPos - 2, castlingRookFromTo =
            Pair(Position(0, 0), Position(0, 3))))
    }

    private fun whiteKingSideCastling(gameState: BitmapGameState): Collection<Move> {
        if (gameState.castlingRights and WHITE_KING_SIDE_CASTLING == 0) {
            return emptyList()
        }
        val attackMap = getPlayerAttackMap(gameState, Player.BLACK)
        val occupied = gameState.allPieces
        val kingPos = extractPositions(gameState.whiteKing).first()
        val requiredEmptyPositions = 0b11UL shl 5
        val requiredNotInCheckPositions = 0b111UL shl 4
        if (requiredEmptyPositions and occupied != 0UL) {
            return emptyList()
        }
        if (requiredNotInCheckPositions and attackMap != 0UL) {
            return emptyList()
        }
        return listOf(createMove(fromPos = kingPos, toPos = kingPos + 2, castlingRookFromTo =
            Pair(Position(0, 7), Position(0, 5))))
    }

    private fun blackQueenSideCastling(gameState: BitmapGameState): Collection<Move> {
        if (gameState.castlingRights and BLACK_QUEEN_SIDE_CASTLING == 0) {
            return emptyList()
        }
        val attackMap = getPlayerAttackMap(gameState, Player.WHITE)
        val occupied = gameState.allPieces
        val kingPos = extractPositions(gameState.blackKing).first()
        val requiredEmptyPositions = 0b111UL shl 57
        val requiredNotInCheckPositions = 0b111UL shl 58
        if (requiredEmptyPositions and occupied != 0UL) {
            return emptyList()
        }
        if (requiredNotInCheckPositions and attackMap != 0UL) {
            return emptyList()
        }
        return listOf(createMove(fromPos = kingPos, toPos = kingPos - 2, castlingRookFromTo =
            Pair(Position(7, 0), Position(7, 3))))
    }

    private fun blackKingSideCastling(gameState: BitmapGameState): Collection<Move> {
        if (gameState.castlingRights and BLACK_KING_SIDE_CASTLING == 0) {
            return emptyList()
        }
        val attackMap = getPlayerAttackMap(gameState, Player.WHITE)
        val occupied = gameState.allPieces
        val kingPos = extractPositions(gameState.blackKing).first()
        val requiredEmptyPositions = 0b11UL shl 61
        val requiredNotInCheckPositions = 0b111UL shl 60
        if (requiredEmptyPositions and occupied != 0UL) {
            return emptyList()
        }
        if (requiredNotInCheckPositions and attackMap != 0UL) {
            return emptyList()
        }
        return listOf(
            createMove(
                fromPos = kingPos, toPos = kingPos + 2,
                castlingRookFromTo =
                    Pair(Position(7, 7), Position(7, 5))
            )
        )
    }
}