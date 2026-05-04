package dev.smueller.checkmate

enum class Color {
    WHITE,
    BLACK;

    fun opposite(): Color = if (this == WHITE) BLACK else WHITE
}
