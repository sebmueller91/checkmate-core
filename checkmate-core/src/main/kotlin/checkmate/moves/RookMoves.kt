package checkmate.moves

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateStraightRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object RookMoves: PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> {
        val moves = mutableListOf<Move>()
        val rooks = if (gameState.isWhiteTurn) gameState.whiteRooks else gameState.blackRooks
        val opponentPieces = if (gameState.isWhiteTurn) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        for (fromPos in extractPositions(rooks)) {
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

        return moves
    }

    override fun generateMoves(gameState: BitmapGameState): List<Move> =
        KingMoves.generatePseudoLegalMoves(gameState).filter { isLegalMove(it, gameState) }.toMutableList()

    override fun getAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val rooks = if (player == Player.WHITE) gameState.whiteRooks else gameState.blackRooks
        val opponentPieces = if (player == Player.WHITE) gameState.blackPieces else gameState.whitePieces
        val occupied = gameState.allPieces

        var attackMap = 0UL
        for (fromPos in extractPositions(rooks)) {
            val reachableSquares = calculateReachableSquares(fromPos, occupied, opponentPieces)

            attackMap = attackMap or reachableSquares
        }

        return attackMap
    }

    private fun calculateReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val westRay = calculateStraightRay(fromPos, step = -1, occupied, opponentPieces)
        val eastRay = calculateStraightRay(fromPos, step = 1, occupied, opponentPieces)
        val northRay = calculateStraightRay(fromPos, step = 8, occupied, opponentPieces)
        val southRay = calculateStraightRay(fromPos, step = -8, occupied, opponentPieces)

        return (westRay or eastRay or northRay or southRay)
    }
}
