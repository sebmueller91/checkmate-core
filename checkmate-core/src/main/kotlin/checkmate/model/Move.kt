package com.github.sebmu91.checkmate.checkmate.model

data class Move(
    val from: Position,
    val to: Position,
    val promotion: Piece? = null
)
