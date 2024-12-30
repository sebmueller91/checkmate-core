package checkmate.util

internal fun extractPositions(bitboard: ULong): List<Int> {
    val positions = mutableListOf<Int>()
    var board = bitboard
    while (board != 0UL) {
        val lsb = board and (board.inv() + 1UL) // Isolate the least significant bit
        positions.add(lsb.countTrailingZeroBits()) // Convert the bit to an index
        board = board xor lsb // Clear the least significant bit
    }
    return positions
}