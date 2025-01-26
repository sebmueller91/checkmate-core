# checkmate-core

## Overview

Checkmate Core is a Kotlin library designed to provide core functionalities for a chess game, including move validation, move generation, and game state management.

## Status

**This library is currently in development and is not yet fully functional.** The core features are still being implemented, and the API is subject to change.

## Features
- Representation of a chess game in high level Kotlin data classes
- Validate chess moves
- Generate valid moves for a given position
- Execute moves and update the game state

## Installation
To include Checkmate Core in your project, add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.sebmu91.checkmate:checkmate-core:0.1.10-SNAPSHOT")
}
```

[![](https://jitpack.io/v/sebmueller91/checkmate-core.svg)](https://jitpack.io/#sebmueller91/checkmate-core)

## Data Structures
The basic structure of this library is a Game object representing a chess game including all history and meta information.
It consists of a list of GameStates. 
Each GameState object represents a snapshot of the game at a specific point in time. It contains the current board state, the player whose turn it is, and other relevant information.
The GameState objects are linked by moves.
If required, it is also possible to update the Game from an older GameState - this will delete all subsequent GameStates and let the game take another path. 

## Usage
Here is a basic example of how to use the Checkmate Core library:
```kotlin
import checkmate.CheckmateCore
import checkmate.impl.CheckmateCoreBuilder
import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position

fun main() {
    // Create the builder
    val builder = CheckmateCoreBuilder()

    // Build the CheckmateCore instance
    val checkmateCore = builder.build()

    // Use the CheckmateCore instance to generate an initial chess game
    val game = checkmateCore.getInitialGame()

    // Get the current game state
    val gameState = game.gameStates.last()

    // Get all moves for a given position
    val moves = gameState.getValidMoves(gameState)

    // Execute the move (this will generate a new game state in the game object)
    val move = checkmateCore.executeMove(move = moves.first(), game = game)

    // Close the builder to clean up the Koin context
    builder.close()
}
