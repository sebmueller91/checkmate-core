# checkmate-core
## Overview

Checkmate Core is a Kotlin library designed to provide core functionalities for a chess game, including move validation, move generation, and game state management.

## Status

**This library is currently in development and is not yet functional.** The core features are still being implemented, and the API is subject to change.

## Features
- Validate chess moves
- Generate valid moves for a given position
- Execute moves and update the game state


## Installation

To include Checkmate Core in your project, add the following dependency to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("com.github.sebmu91.checkmate:checkmate-core:0.1.1-SNAPSHOT")
}
```

## Usage
```kotlin
Here is a basic example of how to use the Checkmate Core library:
import checkmate.CheckmateCore
import checkmate.impl.CheckmateCoreImpl
import checkmate.model.GameState
import checkmate.model.Move
import checkmate.model.Position

val checkmateCore: CheckmateCoreBuilder.build()

val gameState = checkmateCore.generateInitialState()
// Will be updated before initial release...
```