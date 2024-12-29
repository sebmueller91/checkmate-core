package checkmate.moves.movementBitboards

import checkmate.model.Move
import checkmate.model.Position
import checkmate.moves.model.BitmapGameState
import checkmate.moves.model.FILE_H
import checkmate.moves.model.RANK_2
import checkmate.moves.model.RANK_8

internal fun BitmapGameState.generatePawnMovesList(isWhiteTurn: Boolean): List<Move> {
    updateAllPieces()
    return if (isWhiteTurn) generateWhitePawnMovesList() else generateWhitePawnMovesList()
}

internal fun BitmapGameState.generateWhitePawnMovesList(): List<Move> {
    val moves = mutableListOf<Move>()
    val singleMoves = whitePawnSingleMoves()
    val doubleMoves = whitePawnDoubleMoves()
    val leftCaptures = whitePawnLeftCaptures()
    val rightCaptures = whitePawnRightCaptures()

    val singleMovePositions = extractPositions(singleMoves and RANK_8.inv())
    for (toPos in singleMovePositions) {
        val fromPosIndex = toPos - 8
        moves.add(
            Move(
                from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                to = Position(rank = toPos / 8, file = toPos % 8)
            )
        )
    }

    val doubleMovePositions = extractPositions(doubleMoves and RANK_8.inv())
    for (toPos in doubleMovePositions) {
        val fromPosIndex = toPos - 16
        moves.add(
            Move(
                from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                to = Position(rank = toPos / 8, file = toPos % 8)
            )
        )
    }

    val leftCapturePositions = extractPositions(leftCaptures and RANK_8.inv())
    for (toPos in leftCapturePositions) {
        val fromPosIndex = toPos - 7
        moves.add(
            Move(
                from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                to = Position(rank = toPos / 8, file = toPos % 8)
            )
        )
    }

    val rightCapturePositions = extractPositions(rightCaptures and RANK_8.inv())
    for (toPos in rightCapturePositions) {
        val fromPosIndex = toPos - 9
        moves.add(
            Move(
                from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                to = Position(rank = toPos / 8, file = toPos % 8)
            )
        )
    }

    return moves
}

internal fun extractPositions(bitboard: ULong): List<Int> {
    val positions = mutableListOf<Int>()
    var board = bitboard
    while (board != 0UL) {
        val lsb = board and (board.inv() + 1UL) // Isolate the least significant bit
        positions.add(lsb.countTrailingZeroBits()) // Convert the bit to an index
        board = board xor lsb // Clear the least significant bit
    }
    return positions
}

private fun BitmapGameState.whitePawnSingleMoves(): ULong = (whitePawns shl 8) and allPieces.inv()

private fun BitmapGameState.whitePawnDoubleMoves(): ULong {
    val startRank = whitePawns and RANK_2
    val doubleMoves = (startRank shl 16)
    val pathBlocked = (startRank shl 8) and allPieces
    return doubleMoves and pathBlocked.inv() and allPieces.inv()
}

private fun BitmapGameState.whitePawnLeftCaptures(): ULong =(whitePawns shl 7) and blackPieces and FILE_H.inv()
private fun BitmapGameState.whitePawnRightCaptures(): ULong =(whitePawns shl 9) and blackPieces and FILE_H.inv()