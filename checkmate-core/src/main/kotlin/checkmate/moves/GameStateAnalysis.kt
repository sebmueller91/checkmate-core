package checkmate.moves

import checkmate.model.Move
import checkmate.model.Player
import checkmate.moves.model.BitmapGameState
import checkmate.moves.type.*

internal fun getPlayerAttackMap(gameState: BitmapGameState, player: Player): ULong {
    var attackMap = 0UL
    attackMap = PawnMoves.generateAttackMap(gameState, player) or attackMap
    attackMap = RookMoves.generateAttackMap(gameState, player) or attackMap
    attackMap = BishopMoves.generateAttackMap(gameState, player) or attackMap
    attackMap = KnightMoves.generateAttackMap(gameState, player) or attackMap
    attackMap = QueenMoves.generateAttackMap(gameState, player) or attackMap
    attackMap = KingMoves.generateAttackMap(gameState, player) or attackMap
    return attackMap
}

internal fun isKingInCheck(gameState: BitmapGameState, player: Player): Boolean {
    val attackMap = getPlayerAttackMap(gameState, player.opponent())
    val kingBitboard = if (player == Player.WHITE) gameState.whiteKing else gameState.blackKing
    return attackMap and kingBitboard != 0UL
}

internal fun isLegalMove(gameState: BitmapGameState, move: Move): Boolean {
    val newGameState = gameState.executeMove(move)
    return !isKingInCheck(newGameState, gameState.getPlayer())
}

internal fun isStalemate(gameState: BitmapGameState): Boolean {
    return !isKingInCheck(gameState, gameState.getPlayer()) && !canPerformMove(gameState)
}

internal fun isCheckmate(gameState: BitmapGameState): Boolean {
    return isKingInCheck(gameState, gameState.getPlayer()) && !canPerformMove(gameState)
}

private fun canPerformMove(gameState: BitmapGameState): Boolean =
    PawnMoves.generateLegalMoves(gameState).isNotEmpty()
            || RookMoves.generateLegalMoves(gameState).isNotEmpty()
            || BishopMoves.generateLegalMoves(gameState).isNotEmpty()
            || KnightMoves.generateLegalMoves(gameState).isNotEmpty()
            || QueenMoves.generateLegalMoves(gameState).isNotEmpty()
            || KingMoves.generateLegalMoves(gameState).isNotEmpty()

private fun BitmapGameState.getPlayer() = if (isWhiteTurn) Player.WHITE else Player.BLACK