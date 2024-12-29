package checkmate.moves.movementBitboards

import checkmate.model.Move
import checkmate.model.Position
import checkmate.model.Type
import checkmate.moves.model.*

internal fun BitmapGameState.generatePawnMovesList(isWhiteTurn: Boolean): List<Move> {
    updateAllPieces()
    return if (isWhiteTurn) generateWhitePawnMovesList() else generateWhitePawnMovesList()
}

internal fun BitmapGameState.generateWhitePawnMovesList(): List<Move> {
    val moves = mutableListOf<Move>()
    val singleMoves = whitePawnSingleMoves()
    val doubleMoves = whitePawnDoubleMoves()
    val captures = whitePawnCaptures()
    val leftEnPassant = whitePawnLeftEnPassant()
    val rightEnPassant = whitePawnRightEnPassant()

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

    val capturePositions = extractPositions(captures and RANK_8.inv())
    for (toPos in capturePositions) {
        val fromPosIndex = if ((toPos % 8) > (toPos - 9) % 8) toPos - 7 else toPos - 9
        moves.add(
            Move(
                from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                to = Position(rank = toPos / 8, file = toPos % 8)
            )
        )
    }

    val leftEnPassantPositions = extractPositions(leftEnPassant)
    for (toPos in leftEnPassantPositions) {
        val fromPosIndex = toPos - 7
        if (fromPosIndex % 8 >= toPos % 8) {
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                )
            )
        }
    }

    val rightEnPassantPositions = extractPositions(rightEnPassant)
    for (toPos in rightEnPassantPositions) {
        val fromPosIndex = toPos - 9
        if (fromPosIndex % 8 <= toPos % 8) {
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                )
            )
        }
    }

    val promotionPositions = extractPositions(singleMoves and RANK_8)
    for (toPos in promotionPositions) {
        val fromPosIndex = toPos - 8
        val from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8)
        val to = Position(rank = toPos / 8, file = toPos % 8)

        moves.add(Move(from = from, to = to, promotion = Type.QUEEN))
        moves.add(Move(from = from, to = to, promotion = Type.ROOK))
        moves.add(Move(from = from, to = to, promotion = Type.BISHOP))
        moves.add(Move(from = from, to = to, promotion = Type.KNIGHT))
    }

    val promotionCapturePositions = extractPositions(captures and RANK_8)
    for (toPos in promotionCapturePositions) {
        val fromPosIndex = if ((toPos % 8) > (toPos - 9) % 8) toPos - 7 else toPos - 9
        val from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8)
        val to = Position(rank = toPos / 8, file = toPos % 8)

        moves.add(Move(from = from, to = to, promotion = Type.QUEEN))
        moves.add(Move(from = from, to = to, promotion = Type.ROOK))
        moves.add(Move(from = from, to = to, promotion = Type.BISHOP))
        moves.add(Move(from = from, to = to, promotion = Type.KNIGHT))
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

private fun BitmapGameState.whitePawnCaptures(): ULong {
    val leftCapture = (whitePawns shr 7) and blackPieces and FILE_H.inv()
    val rightCapture = (whitePawns shr 9) and blackPieces and FILE_A.inv()
    return leftCapture or rightCapture
}

private fun BitmapGameState.whitePawnLeftEnPassant(): ULong {
    return if (enPassantTarget != 0UL) {
       return (whitePawns shr 7) and enPassantTarget and FILE_H.inv()
    } else 0UL
}

private fun BitmapGameState.whitePawnRightEnPassant(): ULong {
    return if (enPassantTarget != 0UL) {
        return (whitePawns shr 9) and enPassantTarget and FILE_A.inv()
    } else 0UL
}