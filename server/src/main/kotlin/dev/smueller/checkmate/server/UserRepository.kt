package dev.smueller.checkmate.server

import at.favre.lib.crypto.bcrypt.BCrypt
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

data class UserRecord(val id: UUID, val email: String)

class UserRepository(private val bcryptCost: Int = 12) {

    suspend fun register(email: String, password: String): UserRecord? = dbQuery {
        if (Users.selectAll().where { Users.email eq email }.count() > 0L) return@dbQuery null
        val id = Users.insert {
            it[Users.email] = email
            it[passwordHash] = BCrypt.withDefaults().hashToString(bcryptCost, password.toCharArray())
        }[Users.id]
        UserRecord(id, email)
    }

    suspend fun login(email: String, password: String): UserRecord? = dbQuery {
        val row = Users.selectAll().where { Users.email eq email }.singleOrNull() ?: return@dbQuery null
        val hash = row[Users.passwordHash]
        if (!BCrypt.verifyer().verify(password.toCharArray(), hash).verified) return@dbQuery null
        UserRecord(row[Users.id], row[Users.email])
    }

    suspend fun findById(id: UUID): UserRecord? = dbQuery {
        Users.selectAll().where { Users.id eq id }.singleOrNull()?.let {
            UserRecord(it[Users.id], it[Users.email])
        }
    }
}
