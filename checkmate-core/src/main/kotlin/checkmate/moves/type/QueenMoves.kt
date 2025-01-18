package checkmate.moves.type

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.isLegalMove
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateDiagonalRay
import checkmate.util.calculateStraightRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object QueenMoves: PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val moves = mutableListOf<Move>()
        val queens = if (gameState.isWhiteTurn) gameState.whiteQueens else gameState.blackQueens
        val opponentPieces = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        for (fromPos in extractPositions(queens)) {
            val straightReachable = calculateStraightReachableSquares(fromPos, occupied, opponentPieces)
            val diagonalReachable = calculateDiagonalReachableSquares(fromPos, occupied, opponentPieces)

            val reachableSquares = straightReachable or diagonalReachable

            val validMoves = reachableSquares and opponentPieces.inv()
            val captures = reachableSquares and opponentPieces

            moves.addAll(extractPositions(validMoves).map { toPos ->
                createMove(fromPos, toPos, null)
            })

            moves.addAll(extractPositions(captures).map { toPos ->
                createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
            })
        }

        return moves
    }

    override fun generateLegalMoves(gameState: BitmapGameState): List<Move> =
        KingMoves.generatePseudoLegalMoves(gameState).filter { isLegalMove(gameState, it) }.toMutableList()

    override fun generateAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val queens = if (player == Player.WHITE) gameState.whiteQueens else gameState.blackQueens
        val opponentPieces = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        var attackMap = 0UL
        for (fromPos in extractPositions(queens)) {
            val straightReachable = calculateStraightReachableSquares(fromPos, occupied, opponentPieces)
            val diagonalReachable = calculateDiagonalReachableSquares(fromPos, occupied, opponentPieces)

            val reachableSquares = straightReachable or diagonalReachable

            attackMap = attackMap or reachableSquares
        }

        return attackMap
    }

    private fun calculateStraightReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val westRay = calculateStraightRay(fromPos, step = -1, occupied, opponentPieces)
        val eastRay = calculateStraightRay(fromPos, step = 1, occupied, opponentPieces)
        val northRay = calculateStraightRay(fromPos, step = 8, occupied, opponentPieces)
        val southRay = calculateStraightRay(fromPos, step = -8, occupied, opponentPieces)

        return westRay or eastRay or northRay or southRay
    }

    private fun calculateDiagonalReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val northEastRay = calculateDiagonalRay(fromPos, step = 9, occupied, opponentPieces)
        val southWestRay = calculateDiagonalRay(fromPos, step = -9, occupied, opponentPieces)
        val northWestRay = calculateDiagonalRay(fromPos, step = 7, occupied, opponentPieces)
        val southEastRay = calculateDiagonalRay(fromPos, step = -7, occupied, opponentPieces)

        return northEastRay or southWestRay or northWestRay or southEastRay
    }
}