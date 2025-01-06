package checkmate.moves.type

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.isLegalMove
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateDiagonalRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object BishopMoves : PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val moves = mutableListOf<Move>()
        val bishops = if (gameState.isWhiteTurn) gameState.whiteBishops else gameState.blackBishops
        val opponentPieces = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        for (fromPos in extractPositions(bishops)) {
            val reachableSquares = calculateReachableSquares(fromPos, occupied, opponentPieces)

            val validMoves = reachableSquares and opponentPieces.inv()
            val captures = reachableSquares and opponentPieces

            moves.addAll(extractPositions(validMoves).map { toPos ->
                createMove(fromPos, toPos, null)
            })

            moves.addAll(extractPositions(captures).map { toPos ->
                createMove(fromPos, toPos, Position(rank = toPos / 8, file = toPos % 8))
            })
        }

        println(moves.size)
        return moves
    }

    override fun generateMoves(gameState: BitmapGameState): List<Move> =
        KingMoves.generatePseudoLegalMoves(gameState).filter { isLegalMove(gameState, it) }.toMutableList()

    override fun generateAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val bishops = if (player == Player.WHITE) gameState.whiteBishops else gameState.blackBishops
        val opponentPieces = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        var attackMap = 0UL
        for (fromPos in extractPositions(bishops)) {
            val reachableSquares = calculateReachableSquares(fromPos, occupied, opponentPieces)

            attackMap = attackMap or reachableSquares
        }

        return attackMap
    }

    private fun calculateReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val northEastRay = calculateDiagonalRay(fromPos, step = 9, occupied, opponentPieces)
        val southWestRay = calculateDiagonalRay(fromPos, step = -9, occupied, opponentPieces)
        val northWestRay = calculateDiagonalRay(fromPos, step = 7, occupied, opponentPieces)
        val southEastRay = calculateDiagonalRay(fromPos, step = -7, occupied, opponentPieces)

        return (northEastRay or southWestRay or northWestRay or southEastRay)
    }
}