package checkmate.moves

import checkmate.model.Move
import checkmate.model.Player
import checkmate.moves.model.BitmapGameState

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

internal fun isLegalMove(move: Move, gameState: BitmapGameState): Boolean {
    return true
}