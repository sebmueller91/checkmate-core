package dev.smueller.checkmate.server

import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepositoryTest {

    companion object {
        private val counter = AtomicInteger(0)
    }

    private fun setup(): Pair<UserRepository, GameRepository> {
        val url = "jdbc:h2:mem:repo_test_${counter.incrementAndGet()};DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
        DatabaseFactory.init(url, "sa", "")
        return UserRepository(bcryptCost = 4) to GameRepository()
    }

    @Test
    fun registerCreatesUser() = runBlocking {
        val (users, _) = setup()
        val user = users.register("alice@example.com", "password1")
        assertNotNull(user)
        assertEquals("alice@example.com", user.email)
    }

    @Test
    fun registerDuplicateEmailReturnsNull() = runBlocking {
        val (users, _) = setup()
        users.register("bob@example.com", "password1")
        val second = users.register("bob@example.com", "password2")
        assertNull(second)
    }

    @Test
    fun loginWithCorrectPasswordReturnsUser() = runBlocking {
        val (users, _) = setup()
        users.register("carol@example.com", "secret123")
        val result = users.login("carol@example.com", "secret123")
        assertNotNull(result)
        assertEquals("carol@example.com", result.email)
    }

    @Test
    fun loginWithWrongPasswordReturnsNull() = runBlocking {
        val (users, _) = setup()
        users.register("dave@example.com", "correctpass")
        val result = users.login("dave@example.com", "wrongpass")
        assertNull(result)
    }

    @Test
    fun findByIdReturnsUser() = runBlocking {
        val (users, _) = setup()
        val registered = users.register("eve@example.com", "password1")!!
        val found = users.findById(registered.id)
        assertNotNull(found)
        assertEquals(registered.id, found.id)
    }

    @Test
    fun saveGamePersistsRecord() = runBlocking {
        val (users, games) = setup()
        val white = users.register("white@example.com", "password1")!!
        val black = users.register("black@example.com", "password1")!!

        val pgn = "1. e4 e5 2. Qh5 Nc6 3. Bc4 Nf6 4. Qxf7#"
        val id = games.save(pgn, "white", white.id, black.id)
        assertNotNull(id)

        val whiteGames = games.listForUser(white.id)
        assertEquals(1, whiteGames.size)
        assertEquals(pgn, whiteGames[0].pgn)
        assertEquals("white", whiteGames[0].result)
    }

    @Test
    fun listGamesForUserReturnsAllSides() = runBlocking {
        val (users, games) = setup()
        val alice = users.register("alice2@example.com", "password1")!!
        val bob = users.register("bob2@example.com", "password1")!!

        games.save("1. e4", "draw", alice.id, bob.id)
        games.save("1. d4", "white", alice.id, null)

        val aliceGames = games.listForUser(alice.id)
        assertEquals(2, aliceGames.size)

        val bobGames = games.listForUser(bob.id)
        assertEquals(1, bobGames.size)
    }

    @Test
    fun anonymousGameSavedWithNullUserIds() = runBlocking {
        val (_, games) = setup()
        val id = games.save("1. e4 e5", "draw", null, null)
        assertNotNull(id)

        // Arbitrary UUID not in results
        val noGames = games.listForUser(java.util.UUID.randomUUID())
        assertTrue(noGames.isEmpty())
    }
}
