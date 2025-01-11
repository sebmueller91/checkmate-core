package checkmate

import checkmate.model.GameState
import checkmate.model.Player

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