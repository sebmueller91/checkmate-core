package dev.smueller.checkmate.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureApp() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    val jdbcUrl = System.getenv("DATABASE_URL")
        ?: "jdbc:h2:mem:checkmate;DB_CLOSE_DELAY=-1;MODE=PostgreSQL"
    DatabaseFactory.init(jdbcUrl, System.getenv("DATABASE_USER") ?: "sa", System.getenv("DATABASE_PASSWORD") ?: "")

    install(ContentNegotiation) {
        json(Json { encodeDefaults = true; ignoreUnknownKeys = true })
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

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText("Internal error: ${cause.message}")
        }
    }

    configureWebSockets()
    configureRouting()
    configureAuth()
}

val appModule = module {
    single { RoomRegistry() }
    single<EngineService> { RealEngineService() }
    single { UserRepository() }
    single { GameRepository() }
}

// ── Engine service abstraction ───────────────────────────────────────────────

interface EngineService {
    val available: Boolean
    suspend fun bestMove(fen: String, movetimeMs: Int, skillLevel: Int): String
}

class RealEngineService : EngineService {
    private val pool: EnginePool? = runCatching {
        val path = EnginePool.findExecutable() ?: return@runCatching null
        EnginePool(size = 4, executablePath = path)
    }.getOrNull()

    override val available: Boolean get() = pool != null

    override suspend fun bestMove(fen: String, movetimeMs: Int, skillLevel: Int): String =
        pool?.withEngine { engine -> engine.bestMove(fen, movetimeMs, skillLevel) }
            ?: error("Engine not available")
}
