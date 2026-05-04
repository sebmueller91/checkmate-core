package dev.smueller.checkmate.notation

import dev.smueller.checkmate.Color
import dev.smueller.checkmate.Move
import dev.smueller.checkmate.MoveKind
import dev.smueller.checkmate.Piece
import dev.smueller.checkmate.PieceType
import dev.smueller.checkmate.Position
import dev.smueller.checkmate.Square

object Pgn {
    /**
     * Returns the SAN string for [move] in [pos] (position BEFORE the move is applied).
     */
    fun toSan(pos: Position, move: Move): String {
        val sb = StringBuilder()
        when {
            move.isCastle -> sb.append(if (move.kind == MoveKind.CASTLE_KING) "O-O" else "O-O-O")
            pos.pieceAt(move.from)?.type == PieceType.PAWN -> appendPawnSan(sb, pos, move)
            else -> appendPieceSan(sb, pos, move)
        }
        val next = pos.makeMove(move)
        if (next.isInCheck()) {
            sb.append(if (next.legalMoves().isEmpty()) '#' else '+')
        }
        return sb.toString()
    }

    private fun appendPawnSan(sb: StringBuilder, pos: Position, move: Move) {
        if (move.isCapture || move.isEnPassant) {
            sb.append('a' + move.from.file)
            sb.append('x')
        }
        sb.append(move.to.notation())
        if (move.isPromotion) {
            sb.append('=')
            sb.append(move.promotionPiece!!.sanLetter())
        }
    }

    private fun appendPieceSan(sb: StringBuilder, pos: Position, move: Move) {
        val pieceType = pos.pieceAt(move.from)!!.type
        sb.append(pieceType.sanLetter())
        sb.append(disambiguate(pos, move, pieceType))
        if (move.isCapture) sb.append('x')
        sb.append(move.to.notation())
    }

    private fun disambiguate(pos: Position, move: Move, pieceType: PieceType): String {
        val ambiguous = pos.legalMoves().filter { other ->
            other != move &&
            other.to == move.to &&
            pos.pieceAt(other.from)?.type == pieceType
        }
        if (ambiguous.isEmpty()) return ""
        val fromFile = move.from.file
        val fromRank = move.from.rank
        if (ambiguous.none { it.from.file == fromFile }) return ('a' + fromFile).toString()
        if (ambiguous.none { it.from.rank == fromRank }) return (fromRank + 1).toString()
        return ('a' + fromFile).toString() + (fromRank + 1).toString()
    }

    /**
     * Parses a SAN string in the context of [pos] and returns the corresponding [Move].
     */
    fun parseSan(pos: Position, san: String): Move {
        val s = san.trimEnd('+', '#', '?', '!')
        if (s == "O-O" || s == "0-0") {
            return pos.legalMoves().firstOrNull { it.kind == MoveKind.CASTLE_KING }
                ?: error("No kingside castling in: ${pos.toFen()}")
        }
        if (s == "O-O-O" || s == "0-0-0") {
            return pos.legalMoves().firstOrNull { it.kind == MoveKind.CASTLE_QUEEN }
                ?: error("No queenside castling in: ${pos.toFen()}")
        }

        var remaining = s
        var promoType: PieceType? = null
        val promoIdx = remaining.indexOf('=')
        if (promoIdx != -1) {
            promoType = PieceType.fromSanLetter(remaining[promoIdx + 1])
            remaining = remaining.substring(0, promoIdx)
        }

        remaining = remaining.replace("x", "")
        require(remaining.length >= 2) { "SAN too short: $san" }

        val toSq = Square.parse(remaining.takeLast(2))
        val prefix = remaining.dropLast(2)

        return if (prefix.isEmpty() || (prefix.length == 1 && prefix[0].isLowerCase())) {
            parsePawnMove(pos, prefix, toSq, promoType, san)
        } else {
            parsePieceMove(pos, prefix, toSq, san)
        }
    }

    private fun parsePawnMove(
        pos: Position,
        prefix: String,
        toSq: Square,
        promoType: PieceType?,
        san: String,
    ): Move {
        val fromFile = if (prefix.isNotEmpty()) prefix[0] - 'a' else -1
        val candidates = pos.legalMoves().filter { move ->
            move.to == toSq &&
            pos.pieceAt(move.from)?.type == PieceType.PAWN &&
            (fromFile == -1 || move.from.file == fromFile) &&
            (promoType == null) == (!move.isPromotion) &&
            (promoType == null || move.promotionPiece == promoType)
        }
        require(candidates.isNotEmpty()) { "No legal move for SAN \"$san\" in: ${pos.toFen()}" }
        return candidates.first()
    }

    private fun parsePieceMove(pos: Position, prefix: String, toSq: Square, san: String): Move {
        val pieceType = PieceType.fromSanLetter(prefix[0])
        val disambig = prefix.drop(1)
        val candidates = pos.legalMoves().filter { move ->
            move.to == toSq &&
            pos.pieceAt(move.from)?.type == pieceType &&
            matchesDisambig(move.from, disambig)
        }
        require(candidates.size == 1) {
            "Expected 1 candidate for SAN \"$san\", found ${candidates.size} in: ${pos.toFen()}"
        }
        return candidates.single()
    }

    private fun matchesDisambig(from: Square, disambig: String): Boolean {
        for (c in disambig) {
            if (c.isLetter() && from.file != c - 'a') return false
            if (c.isDigit() && from.rank != c - '1') return false
        }
        return true
    }

    /**
     * Formats [moves] as PGN movetext (e.g., "1. e4 e5 2. Nf3 Nc6").
     * [startPos] is the position before the first move.
     */
    fun formatMovetext(startPos: Position, moves: List<Move>): String {
        val sb = StringBuilder()
        var pos = startPos
        var moveNum = pos.fullmoveNumber
        var isWhite = pos.sideToMove == Color.WHITE

        for ((i, move) in moves.withIndex()) {
            if (isWhite) {
                if (i > 0) sb.append(' ')
                sb.append(moveNum).append('.')
            }
            sb.append(' ')
            sb.append(toSan(pos, move))
            if (!isWhite) moveNum++
            pos = pos.makeMove(move)
            isWhite = !isWhite
        }
        return sb.toString()
    }

    /**
     * Parses PGN movetext (e.g., "1. e4 e5 2. Nf3") starting from [startPos].
     */
    fun parseMovetext(startPos: Position, movetext: String): List<Move> {
        val tokens = movetext.trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() && !it.matches(Regex("\\d+\\.+")) }
        val moves = mutableListOf<Move>()
        var pos = startPos
        for (token in tokens) {
            val move = parseSan(pos, token)
            moves.add(move)
            pos = pos.makeMove(move)
        }
        return moves
    }
}

private fun PieceType.sanLetter(): Char = when (this) {
    PieceType.KNIGHT -> 'N'
    PieceType.BISHOP -> 'B'
    PieceType.ROOK -> 'R'
    PieceType.QUEEN -> 'Q'
    PieceType.KING -> 'K'
    PieceType.PAWN -> error("Pawns have no SAN letter")
}

private fun PieceType.Companion.fromSanLetter(c: Char): PieceType = when (c) {
    'N' -> PieceType.KNIGHT
    'B' -> PieceType.BISHOP
    'R' -> PieceType.ROOK
    'Q' -> PieceType.QUEEN
    'K' -> PieceType.KING
    else -> error("Unknown piece SAN letter: $c")
}

private fun Piece.sanLetter(): Char = type.sanLetter()
