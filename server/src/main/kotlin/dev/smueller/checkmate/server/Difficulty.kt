package dev.smueller.checkmate.server

enum class Difficulty(val skillLevel: Int, val movetimeMs: Int) {
    BEGINNER(skillLevel = 1,  movetimeMs = 100),
    EASY    (skillLevel = 5,  movetimeMs = 200),
    MEDIUM  (skillLevel = 10, movetimeMs = 500),
    HARD    (skillLevel = 15, movetimeMs = 1000),
    EXPERT  (skillLevel = 20, movetimeMs = 2000);

    companion object {
        fun fromString(s: String): Difficulty =
            entries.firstOrNull { it.name.equals(s, ignoreCase = true) } ?: MEDIUM
    }
}
