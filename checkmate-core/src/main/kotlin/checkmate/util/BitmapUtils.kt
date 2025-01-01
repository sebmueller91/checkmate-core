package checkmate.util

internal fun extractPositions(bitboard: ULong): List<Int> {
    return generateSequence(bitboard) { it and (it - 1UL) } // Iteratively clear the least significant bit
        .takeWhile { it != 0UL } // Stop when no bits are set
        .map { it.countTrailingZeroBits() } // Map each isolated bit to its index
        .toList() // Convert the sequence to a list
}

internal fun ULong.printAsBoard(title: String = "") {
    if (title.isNotBlank()) println(title)
    println("  0 1 2 3 4 5 6 7")
    for (rank in 7 downTo 0) {
        print("$rank ")
        for (file in 0..7) {
            val position = rank * 8 + file
            if ((this and (1UL shl position)) != 0UL) {
                print("# ")
            } else {
                print(". ")
            }
        }
        println(" $rank")
    }
    println("  0 1 2 3 4 5 6 7")
    println()
}

