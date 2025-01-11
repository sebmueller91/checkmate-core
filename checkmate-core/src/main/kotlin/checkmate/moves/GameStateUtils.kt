package checkmate.moves

import checkmate.model.Move
import checkmate.model.Player
import checkmate.moves.model.BitmapGameState
import checkmate.moves.type.*
import checkmate.util.printAsBoard

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

internal fun isKingInCheck(gameState: BitmapGameState, player: Player): Boolean { // TODO: Test
    val attackMap = getPlayerAttackMap(gameState, player.opponent())
    val kingBitboard = if (player == Player.WHITE) gameState.whiteKing else gameState.blackKing
    attackMap.printAsBoard("Attack map")
    kingBitboard.printAsBoard("King")
    (attackMap and kingBitboard).printAsBoard("King in check")
    return attackMap and kingBitboard != 0UL
}

internal fun isLegalMove(gameState: BitmapGameState, move: Move): Boolean {
    val newGameState = gameState.executeMove(move)
    return !isKingInCheck(newGameState, if (gameState.isWhiteTurn) Player.WHITE else Player.BLACK)
}

internal fun isStalemate(gameState: BitmapGameState): Boolean { // TODO: Test
    val player = if (gameState.isWhiteTurn) Player.WHITE else Player.BLACK
    return !isKingInCheck(gameState, player) && !canPerformMove(gameState)
}

internal fun isCheckmate(gameState: BitmapGameState): Boolean { // TODO: Test
    val player = if (gameState.isWhiteTurn) Player.WHITE else Player.BLACK
    return isKingInCheck(gameState, player) && !canPerformMove(gameState)
}

private fun canPerformMove(gameState: BitmapGameState): Boolean =
    PawnMoves.generateMoves(gameState).isNotEmpty()
            || RookMoves.generateMoves(gameState).isNotEmpty()
            || BishopMoves.generateMoves(gameState).isNotEmpty()
            || KnightMoves.generateMoves(gameState).isNotEmpty()
            || QueenMoves.generateMoves(gameState).isNotEmpty()
            || KingMoves.generateMoves(gameState).isNotEmpty()