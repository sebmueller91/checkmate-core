package checkmate

import checkmate.model.GameState
import checkmate.model.Player
import checkmate.moves.model.BitmapGameState

internal fun generateEmptyBoardGameState(): GameState {
    val game = CheckmateCore().generateInitialState()
    return game.gameStates.last().let { state ->
        state.copy(
            currentPlayer = Player.WHITE,
            board = state.board.map { it.toMutableList() }.toMutableList()
                .apply {
                    this[0][0] = null
                    this[0][1] = null
                    this[0][2] = null
                    this[0][3] = null
                    this[0][4] = null
                    this[0][5] = null
                    this[0][6] = null
                    this[0][7] = null
                    this[1][0] = null
                    this[1][1] = null
                    this[1][2] = null
                    this[1][3] = null
                    this[1][4] = null
                    this[1][5] = null
                    this[1][6] = null
                    this[1][7] = null
                    this[6][0] = null
                    this[6][1] = null
                    this[6][2] = null
                    this[6][3] = null
                    this[6][4] = null
                    this[6][5] = null
                    this[6][6] = null
                    this[6][7] = null
                    this[7][0] = null
                    this[7][1] = null
                    this[7][2] = null
                    this[7][3] = null
                    this[7][4] = null
                    this[7][5] = null
                    this[7][6] = null
                    this[7][7] = null
                })
    }
}

internal fun BitmapGameState.compareTo(gameState: BitmapGameState): Boolean {
    var isEqual = true

    if (this.whitePawns != gameState.whitePawns) {
        println("Mismatch in whitePawns: ${this.whitePawns} != ${gameState.whitePawns}")
        isEqual = false
    }
    if (this.whiteKnights != gameState.whiteKnights) {
        println("Mismatch in whiteKnights: ${this.whiteKnights} != ${gameState.whiteKnights}")
        isEqual = false
    }
    if (this.whiteBishops != gameState.whiteBishops) {
        println("Mismatch in whiteBishops: ${this.whiteBishops} != ${gameState.whiteBishops}")
        isEqual = false
    }
    if (this.whiteRooks != gameState.whiteRooks) {
        println("Mismatch in whiteRooks: ${this.whiteRooks} != ${gameState.whiteRooks}")
        isEqual = false
    }
    if (this.whiteQueens != gameState.whiteQueens) {
        println("Mismatch in whiteQueens: ${this.whiteQueens} != ${gameState.whiteQueens}")
        isEqual = false
    }
    if (this.whiteKing != gameState.whiteKing) {
        println("Mismatch in whiteKing: ${this.whiteKing} != ${gameState.whiteKing}")
        isEqual = false
    }
    if (this.blackPawns != gameState.blackPawns) {
        println("Mismatch in blackPawns: ${this.blackPawns} != ${gameState.blackPawns}")
        isEqual = false
    }
    if (this.blackKnights != gameState.blackKnights) {
        println("Mismatch in blackKnights: ${this.blackKnights} != ${gameState.blackKnights}")
        isEqual = false
    }
    if (this.blackBishops != gameState.blackBishops) {
        println("Mismatch in blackBishops: ${this.blackBishops} != ${gameState.blackBishops}")
        isEqual = false
    }
    if (this.blackRooks != gameState.blackRooks) {
        println("Mismatch in blackRooks: ${this.blackRooks} != ${gameState.blackRooks}")
        isEqual = false
    }
    if (this.blackQueens != gameState.blackQueens) {
        println("Mismatch in blackQueens: ${this.blackQueens} != ${gameState.blackQueens}")
        isEqual = false
    }
    if (this.blackKing != gameState.blackKing) {
        println("Mismatch in blackKing: ${this.blackKing} != ${gameState.blackKing}")
        isEqual = false
    }
    if (this.whitePieces != gameState.whitePieces) {
        println("Mismatch in whitePieces: ${this.whitePieces} != ${gameState.whitePieces}")
        isEqual = false
    }
    if (this.blackPieces != gameState.blackPieces) {
        println("Mismatch in blackPieces: ${this.blackPieces} != ${gameState.blackPieces}")
        isEqual = false
    }
    if (this.allPieces != gameState.allPieces) {
        println("Mismatch in allPieces: ${this.allPieces} != ${gameState.allPieces}")
        isEqual = false
    }
    if (this.castlingRights != gameState.castlingRights) {
        println("Mismatch in castlingRights: ${this.castlingRights} != ${gameState.castlingRights}")
        isEqual = false
    }
    if (this.enPassantTarget != gameState.enPassantTarget) {
        println("Mismatch in enPassantTarget: ${this.enPassantTarget} != ${gameState.enPassantTarget}")
        isEqual = false
    }

    return isEqual
}