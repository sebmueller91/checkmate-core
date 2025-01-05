package checkmate.moves

import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateStraightRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object RookMoves: PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> =
        gameState.getPseudoLegalMoves()

    override fun generateMoves(gameState: BitmapGameState): List<Move> {
        TODO("Not yet implemented")
    }

    private fun BitmapGameState.getPseudoLegalMoves(): List<Move> {
        val moves = mutableListOf<Move>()
        val rooks = if (isWhiteTurn) whiteRooks else blackRooks
        val opponentPieces = if (isWhiteTurn) blackPieces else whitePieces
        val occupied = allPieces

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

    private fun calculateReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val westRay = calculateStraightRay(fromPos, step = -1, occupied, opponentPieces)
        val eastRay = calculateStraightRay(fromPos, step = 1, occupied, opponentPieces)
        val northRay = calculateStraightRay(fromPos, step = 8, occupied, opponentPieces)
        val southRay = calculateStraightRay(fromPos, step = -8, occupied, opponentPieces)

        return (westRay or eastRay or northRay or southRay)
    }
}
