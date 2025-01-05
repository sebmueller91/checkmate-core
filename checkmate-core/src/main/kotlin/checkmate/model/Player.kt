package checkmate.model

enum class Player {
    BLACK,
    WHITE;

    fun opponent(): Player =
        when (this) {
            Player.BLACK -> Player.WHITE
            Player.WHITE -> Player.BLACK
        }
}