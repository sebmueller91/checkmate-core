package checkmate.moves

import checkmate.model.Player
import checkmate.moves.model.BitmapGameState

internal class MovesUtils {
    fun getAttackMap(gameState: BitmapGameState, player: Player): ULong {
        var attackMap = 0UL
        attackMap = PawnMoves.getAttackMap(gameState, player) or attackMap
        attackMap = RookMoves.getAttackMap(gameState, player) or attackMap
        attackMap = BishopMoves.getAttackMap(gameState, player) or attackMap
        attackMap = KingMoves.getAttackMap(gameState, player) or attackMap
        attackMap = QueenMoves.getAttackMap(gameState, player) or attackMap
        attackMap = KingMoves.getAttackMap(gameState, player) or attackMap
        return attackMap
    }

    fun isKingInCheck(gameState: BitmapGameState, player: Player): Boolean {
        val attackMap = getAttackMap(gameState, player.opponent())
        val kingBitboard = if (player == Player.WHITE) gameState.whiteKing else gameState.blackKing
        return attackMap and kingBitboard != 0UL
    }
}