package dev.smueller.checkmate

import kotlin.jvm.JvmInline

@JvmInline
value class Square(val index: Int) {
    init {
        require(index in 0..63) { "Square index out of range: $index" }
    }

    val file: Int get() = index and 7
    val rank: Int get() = index ushr 3

    val bitboard: ULong get() = 1uL shl index

    fun notation(): String = "${'a' + file}${'1' + rank}"

    override fun toString(): String = notation()

    companion object {
        fun of(file: Int, rank: Int): Square {
            require(file in 0..7) { "File out of range: $file" }
            require(rank in 0..7) { "Rank out of range: $rank" }
            return Square(rank * 8 + file)
        }

        fun parse(s: String): Square {
            require(s.length == 2) { "Invalid square notation: $s" }
            val file = s[0].lowercaseChar() - 'a'
            val rank = s[1] - '1'
            return of(file, rank)
        }

        // Common squares used by castling logic and tests.
        val A1 = Square(0); val B1 = Square(1); val C1 = Square(2); val D1 = Square(3)
        val E1 = Square(4); val F1 = Square(5); val G1 = Square(6); val H1 = Square(7)
        val A8 = Square(56); val B8 = Square(57); val C8 = Square(58); val D8 = Square(59)
        val E8 = Square(60); val F8 = Square(61); val G8 = Square(62); val H8 = Square(63)
    }
}
