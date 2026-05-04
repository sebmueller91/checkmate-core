package dev.smueller.checkmate.server

import java.util.UUID

data class GameEndRecord(
    val movetext: String,
    val result: String,
    val whiteUserId: UUID?,
    val blackUserId: UUID?,
)
