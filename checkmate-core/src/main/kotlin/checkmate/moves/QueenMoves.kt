package checkmate.moves

import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.util.calculateDiagonalRay
import checkmate.util.calculateStraightRay
import checkmate.util.createMove
import checkmate.util.extractPositions

internal fun BitmapGameState.generateQueenMoves(isWhiteTurn: Boolean): List<Move> {
    val moves = mutableListOf<Move>()
    val queens = if (isWhiteTurn) whiteQueens else blackQueens
    val opponentPieces = if (isWhiteTurn) blackPieces else whitePieces
    val occupied = allPieces

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