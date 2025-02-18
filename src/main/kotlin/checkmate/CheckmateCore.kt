package checkmate

import checkmate.model.Game
import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position

/**
 * The CheckmateCore interface defines the core functionalities required for managing a chess game.
 */
interface CheckmateCore {

    /**
     * Generates the initial state of the game.
     *
     * @return the initial Game object representing the starting state of the game.
     */
    fun getInitialGame(): Game

    /**
     * Retrieves a list of valid moves for the given game state.
     *
     * @param gameState the current state of the game.
     * @return a list of valid Move objects.
     */
    fun getValidMoves(gameState: GameState): List<Move>

    /**
     * Checks if a given move is valid for the current game state.
     *
     * @param gameState the current state of the game.
     * @param move the move to be validated.
     * @return true if the move is valid, false otherwise.
     */
    fun isValidMove(gameState: GameState, move: Move): Boolean =
        move in getValidMoves(gameState)

    /**
     * Retrieves a list of valid moves for a specific position in the given game state.
     *
     * @param gameState the current state of the game.
     * @param position the position on the board to get valid moves for.
     * @return a list of valid Move objects for the specified position.
     * @throws InvalidPositionException if the position is not valid.
     */
    fun getValidMoves(gameState: GameState, position: Position): List<Move>

    /**
     * Executes a move in the given game.
     *
     * @param move the move to be executed.
     * @param game the game in which the move is to be executed.
     * @param moveIndex an optional move index specifying the index of the game state the move should be applied to. This will delete all subsequent game states!
     * @return the updated game after executing the move.
     * @throws InvalidMoveException if the move is not valid.
     * @throws InvalidParameterException if the move index is incorrect.
     */
    fun executeMove(move: Move, game: Game, moveIndex: Int? = null): Game
}