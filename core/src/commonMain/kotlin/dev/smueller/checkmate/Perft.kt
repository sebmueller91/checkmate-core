package dev.smueller.checkmate

/**
 * Perft (performance test): node count at fixed depth, used as the canonical
 * correctness test for move generation. Internal — exposed for tests only.
 */
internal fun perft(pos: Position, depth: Int): Long {
    if (depth == 0) return 1
    val moves = pos.legalMoves()
    if (depth == 1) return moves.size.toLong()
    var nodes = 0L
    for (m in moves) nodes += perft(pos.makeMove(m), depth - 1)
    return nodes
}

/** Per-root-move node counts — invaluable for diffing against a reference engine. */
internal fun perftDivide(pos: Position, depth: Int): Map<String, Long> {
    val result = LinkedHashMap<String, Long>()
    val moves = pos.legalMoves().sortedBy { it.toUci() }
    for (m in moves) result[m.toUci()] = perft(pos.makeMove(m), depth - 1)
    return result
}
