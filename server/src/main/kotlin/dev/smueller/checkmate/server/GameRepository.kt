package dev.smueller.checkmate.server

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import java.time.LocalDateTime
import java.util.UUID

data class GameRecord(
    val id: UUID,
    val pgn: String,
    val result: String,
    val whiteUserId: UUID?,
    val blackUserId: UUID?,
    val createdAt: LocalDateTime,
)

class GameRepository {

    suspend fun save(pgn: String, result: String, whiteUserId: UUID?, blackUserId: UUID?): UUID = dbQuery {
        Games.insert {
            it[Games.pgn] = pgn
            it[Games.result] = result
            it[Games.whiteUserId] = whiteUserId
            it[Games.blackUserId] = blackUserId
        }[Games.id]
    }

    suspend fun listForUser(userId: UUID): List<GameRecord> = dbQuery {
        Games.selectAll()
            .where { (Games.whiteUserId eq userId) or (Games.blackUserId eq userId) }
            .orderBy(Games.createdAt, SortOrder.DESC)
            .map { row ->
                GameRecord(
                    id = row[Games.id],
                    pgn = row[Games.pgn],
                    result = row[Games.result],
                    whiteUserId = row[Games.whiteUserId],
                    blackUserId = row[Games.blackUserId],
                    createdAt = row[Games.createdAt],
                )
            }
    }
}
