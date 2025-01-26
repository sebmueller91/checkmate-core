package checkmate.model

/**
 * Represents the status of a chess game.
 * States like draw by threefold, runout of time, etc. are not considered here.
 * They depend on the game mode and must be implemented by the user of the library.
 */
enum class GameStatus {
    /**
     * The game is still ongoing.
     */
    ONGOING,

    /**
     * The game is in a stalemate with White to move.
     */
    STALEMATE_WHITE,

    /**
     * The game is in a stalemate with Black to move.
     */
    STALEMATE_BLACK,

    /**
     * The game has ended with White checkmated.
     */
    CHECKMATE_WHITE,

    /**
     * The game has ended with Black checkmated.
     */
    CHECKMATE_BLACK,
}