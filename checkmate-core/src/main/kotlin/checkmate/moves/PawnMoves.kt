package checkmate.moves

import checkmate.model.Move
import checkmate.model.Player
import checkmate.model.Position
import checkmate.model.Type
import checkmate.moves.model.*
import checkmate.util.extractPositions

internal object PawnMoves: PieceMoves() {
    override fun generatePseudoLegalMoves(gameState: BitmapGameState): List<Move> =
        if (gameState.isWhiteTurn) gameState.getWhitePawnMoves() else gameState.getBlackPawnMoves()

    override fun generateMoves(gameState: BitmapGameState): List<Move> {
        TODO("Not yet implemented")
    }

    override fun getAttackMap(gameState: BitmapGameState, player: Player): ULong {
        val leftCaptures = if (player == Player.WHITE) gameState.whitePawnLeftCaptures() else gameState.blackPawnLeftCaptures()
        val rightCaptures = if (player == Player.WHITE) gameState.blackPawnRightCaptures() else gameState.blackPawnRightCaptures()
        return leftCaptures or rightCaptures
    }

    private fun BitmapGameState.getWhitePawnMoves(): List<Move> {
        val moves = mutableListOf<Move>()
        val singleMoves = whitePawnSingleMoves()
        val doubleMoves = whitePawnDoubleMoves()
        val leftCaptures = whitePawnLeftCaptures()
        val rightCaptures = whitePawnRightCaptures()
        val enPassantLeftCaptures = whitePawnLeftEnPassant()
        val enPassantRightCaptures = whitePawnRightEnPassant()

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
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val rightCapturePositions = extractPositions(rightCaptures and RANK_8.inv())
        for (toPos in rightCapturePositions) {
            val fromPosIndex = toPos - 9
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val enPassantLeftCapturePositions = extractPositions(enPassantLeftCaptures and RANK_8.inv())
        for (toPos in enPassantLeftCapturePositions) {
            val fromPosIndex = toPos - 7
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = (toPos / 8) - 1, file = toPos % 8)
                )
            )
        }

        val enPassantRightCapturePositions = extractPositions(enPassantRightCaptures and RANK_8.inv())
        for (toPos in enPassantRightCapturePositions) {
            val fromPosIndex = toPos - 9
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = (toPos / 8) - 1, file = toPos % 8)
                )
            )
        }

        val forwardPromotionPositions = extractPositions(singleMoves and RANK_8)
        for (toPos in forwardPromotionPositions) {
            val fromPosIndex = toPos - 8
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val leftCapturePromotionPositions = extractPositions(leftCaptures and RANK_8)
        for (toPos in leftCapturePromotionPositions) {
            val fromPosIndex = toPos - 7
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val rightCapturePromotionPositions = extractPositions(rightCaptures and RANK_8)
        for (toPos in rightCapturePromotionPositions) {
            val fromPosIndex = toPos - 9
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        return moves
    }

    private fun BitmapGameState.getBlackPawnMoves(): List<Move> {
        val moves = mutableListOf<Move>()
        val singleMoves = blackPawnSingleMoves()
        val doubleMoves = blackPawnDoubleMoves()
        val leftCaptures = blackPawnLeftCaptures()
        val rightCaptures = blackPawnRightCaptures()
        val enPassantLeftCaptures = blackPawnLeftEnPassant()
        val enPassantRightCaptures = blackPawnRightEnPassant()

        val singleMovePositions = extractPositions(singleMoves and RANK_1.inv())
        for (toPos in singleMovePositions) {
            val fromPosIndex = toPos + 8
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val doubleMovePositions = extractPositions(doubleMoves and RANK_1.inv())
        for (toPos in doubleMovePositions) {
            val fromPosIndex = toPos + 16
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val leftCapturePositions = extractPositions(leftCaptures and RANK_1.inv())
        for (toPos in leftCapturePositions) {
            val fromPosIndex = toPos + 9
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val rightCapturePositions = extractPositions(rightCaptures and RANK_1.inv())
        for (toPos in rightCapturePositions) {
            val fromPosIndex = toPos + 7
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val enPassantLeftCapturePositions = extractPositions(enPassantLeftCaptures and RANK_1.inv())
        for (toPos in enPassantLeftCapturePositions) {
            val fromPosIndex = toPos + 9
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = (toPos / 8) + 1, file = toPos % 8)
                )
            )
        }

        val enPassantRightCapturePositions = extractPositions(enPassantRightCaptures and RANK_1.inv())
        for (toPos in enPassantRightCapturePositions) {
            val fromPosIndex = toPos + 7
            moves.add(
                Move(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = (toPos / 8) + 1, file = toPos % 8)
                )
            )
        }

        val forwardPromotionPositions = extractPositions(singleMoves and RANK_1)
        for (toPos in forwardPromotionPositions) {
            val fromPosIndex = toPos + 8
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val leftCapturePromotionPositions = extractPositions(leftCaptures and RANK_1)
        for (toPos in leftCapturePromotionPositions) {
            val fromPosIndex = toPos + 9
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        val rightCapturePromotionPositions = extractPositions(rightCaptures and RANK_1)
        for (toPos in rightCapturePromotionPositions) {
            val fromPosIndex = toPos + 7
            moves.addAll(
                getPromotionMoves(
                    from = Position(rank = fromPosIndex / 8, file = fromPosIndex % 8),
                    to = Position(rank = toPos / 8, file = toPos % 8),
                    capture = Position(rank = toPos / 8, file = toPos % 8)
                )
            )
        }

        return moves
    }

    private fun getPromotionMoves(from: Position, to: Position, capture: Position? = null): List<Move> = listOf(
        Move(
            from = from,
            to = to,
            capture = capture,
            promotion = Type.QUEEN
        ),
        Move(
            from = from,
            to = to,
            capture = capture,
            promotion = Type.BISHOP
        ),
        Move(
            from = from,
            to = to,
            capture = capture,
            promotion = Type.KNIGHT
        ),
        Move(
            from = from,
            to = to,
            capture = capture,
            promotion = Type.ROOK
        )
    )

    private fun BitmapGameState.whitePawnSingleMoves(): ULong = (whitePawns shl 8) and allPieces.inv()

    private fun BitmapGameState.whitePawnDoubleMoves(): ULong {
        val startRank = whitePawns and RANK_2
        val doubleMoves = (startRank shl 16)
        val pathBlocked = (startRank shl 8) and allPieces
        return doubleMoves and pathBlocked.inv() and allPieces.inv()
    }

    private fun BitmapGameState.whitePawnLeftEnPassant(): ULong {
        return if (enPassantTarget != 0UL) {
            (whitePawns shl 7) and enPassantTarget and FILE_H.inv()
        } else 0UL
    }

    private fun BitmapGameState.whitePawnRightEnPassant(): ULong {
        return if (enPassantTarget != 0UL) {
            (whitePawns shl 9) and enPassantTarget and FILE_A.inv()
        } else 0UL
    }

    private fun BitmapGameState.blackPawnSingleMoves(): ULong = (blackPawns shr 8) and allPieces.inv()

    private fun BitmapGameState.blackPawnDoubleMoves(): ULong {
        val startRank = blackPawns and RANK_7
        val doubleMoves = (startRank shr 16)
        val pathBlocked = (startRank shr 8) and allPieces
        return doubleMoves and pathBlocked.inv() and allPieces.inv()
    }

    private fun BitmapGameState.whitePawnLeftCaptures(): ULong = (whitePawns shl 7) and blackPieces and FILE_H.inv()

    private fun BitmapGameState.whitePawnRightCaptures(): ULong = (whitePawns shl 9) and blackPieces and FILE_H.inv()

    private fun BitmapGameState.blackPawnLeftCaptures(): ULong = (blackPawns shr 9) and whitePieces and FILE_A.inv()

    private fun BitmapGameState.blackPawnRightCaptures(): ULong = (blackPawns shr 7) and whitePieces and FILE_H.inv()

    private fun BitmapGameState.blackPawnLeftEnPassant(): ULong {
        return if (enPassantTarget != 0UL) {
            (blackPawns shr 9) and enPassantTarget and FILE_A.inv()
        } else 0UL
    }

    private fun BitmapGameState.blackPawnRightEnPassant(): ULong {
        return if (enPassantTarget != 0UL) {
            (blackPawns shr 7) and enPassantTarget and FILE_H.inv()
        } else 0UL
    }
}
