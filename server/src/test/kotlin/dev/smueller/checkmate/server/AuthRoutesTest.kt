package dev.smueller.checkmate.server

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRoutesTest {

    companion object {
        private val dbCounter = AtomicInteger(0)
    }

    private fun freshDbUrl() =
        "jdbc:h2:mem:auth_test_${dbCounter.incrementAndGet()};DB_CLOSE_DELAY=-1;MODE=PostgreSQL"

    private fun testApp(block: suspend io.ktor.server.testing.ApplicationTestBuilder.() -> Unit) =
        testApplication {
            application {
                DatabaseFactory.init(freshDbUrl(), "sa", "")
                install(Koin) {
                    slf4jLogger()
                    modules(module {
                        single { UserRepository(bcryptCost = 4) }
                        single { GameRepository() }
                    })
                }
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true; encodeDefaults = true })
                }
                install(Authentication) {
                    jwt("auth-jwt") {
                        realm = "checkmate"
                        verifier(JwtConfig.verifier)
                        validate { credential ->
                            credential.payload.getClaim("userId").asString()
                                ?.let { JWTPrincipal(credential.payload) }
                        }
                    }
                }
                configureAuth()
            }
            block()
        }

    @Test
    fun registerCreatesAccount() = testApp {
        val client = createClient { }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"register@example.com","password":"password123"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertNotNull(Json.parseToJsonElement(body).jsonObject["token"])
        assertTrue(body.contains("register@example.com"))
    }

    @Test
    fun duplicateEmailReturnsConflict() = testApp {
        val client = createClient { }
        val req = """{"email":"dup@example.com","password":"password123"}"""
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }
        assertEquals(HttpStatusCode.Conflict, response.status)
    }

    @Test
    fun shortPasswordReturnsBadRequest() = testApp {
        val client = createClient { }
        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"short@example.com","password":"abc"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun loginWithValidCredentialsReturnsToken() = testApp {
        val client = createClient { }
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"login@example.com","password":"password123"}""")
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"login@example.com","password":"password123"}""")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertNotNull(Json.parseToJsonElement(response.bodyAsText()).jsonObject["token"])
    }

    @Test
    fun loginWithWrongPasswordReturnsUnauthorized() = testApp {
        val client = createClient { }
        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"badpass@example.com","password":"password123"}""")
        }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"badpass@example.com","password":"wrongpassword"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun loginWithUnknownEmailReturnsUnauthorized() = testApp {
        val client = createClient { }
        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"nobody@example.com","password":"password123"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun meWithoutTokenReturnsUnauthorized() = testApp {
        val response = client.get("/me")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun meWithValidTokenReturnsUserInfo() = testApp {
        val client = createClient { }
        val regResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"me@example.com","password":"password123"}""")
        }
        val token = Json.parseToJsonElement(regResponse.bodyAsText())
            .jsonObject["token"]!!.jsonPrimitive.content

        val meResponse = client.get("/me") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, meResponse.status)
        assertTrue(meResponse.bodyAsText().contains("me@example.com"))
    }

    @Test
    fun refreshWithValidTokenReturnsNewToken() = testApp {
        val client = createClient { }
        val regResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"refresh@example.com","password":"password123"}""")
        }
        val token = Json.parseToJsonElement(regResponse.bodyAsText()).jsonObject["token"]!!.jsonPrimitive.content

        val refreshResponse = client.post("/auth/refresh") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, refreshResponse.status)
        val newToken = Json.parseToJsonElement(refreshResponse.bodyAsText()).jsonObject["token"]!!.jsonPrimitive.content
        assertNotNull(newToken)
        assertTrue(newToken.isNotBlank())
    }

    @Test
    fun refreshWithoutTokenReturnsUnauthorized() = testApp {
        val response = client.post("/auth/refresh")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun refreshWithInvalidTokenReturnsUnauthorized() = testApp {
        val client = createClient { }
        val response = client.post("/auth/refresh") {
            header("Authorization", "Bearer not.a.valid.token")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun gamesListReturnsEmptyForNewUser() = testApp {
        val client = createClient { }
        val regResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"games@example.com","password":"password123"}""")
        }
        val parsed = Json.parseToJsonElement(regResponse.bodyAsText()).jsonObject
        val userId = parsed["userId"]!!.jsonPrimitive.content
        val token = parsed["token"]!!.jsonPrimitive.content

        val gamesResponse = client.get("/users/$userId/games") {
            header("Authorization", "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, gamesResponse.status)
        assertEquals("[]", gamesResponse.bodyAsText().trim())
    }
}
