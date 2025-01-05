package checkmate.moves

import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateDiagonalRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal object BishopMoves : PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> = gameState.getPseudoLegalMoves()

    override fun generateMoves(gameState: BitmapGameState): List<Move> {
        TODO("Not yet implemented")
    }

    private fun BitmapGameState.getPseudoLegalMoves(): List<Move> {
        val moves = mutableListOf<Move>()
        val bishops = if (isWhiteTurn) whiteBishops else blackBishops
        val opponentPieces = if (isWhiteTurn) blackPieces else whitePieces
        val occupied = allPieces

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

    private fun calculateReachableSquares(fromPos: Int, occupied: ULong, opponentPieces: ULong): ULong {
        val northEastRay = calculateDiagonalRay(fromPos, step = 9, occupied, opponentPieces)
        val southWestRay = calculateDiagonalRay(fromPos, step = -9, occupied, opponentPieces)
        val northWestRay = calculateDiagonalRay(fromPos, step = 7, occupied, opponentPieces)
        val southEastRay = calculateDiagonalRay(fromPos, step = -7, occupied, opponentPieces)

        return (northEastRay or southWestRay or northWestRay or southEastRay)
    }
}