package dev.smueller.checkmate.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.util.UUID

@Serializable data class RegisterRequest(val email: String, val password: String)
@Serializable data class LoginRequest(val email: String, val password: String)
@Serializable data class AuthResponse(val token: String, val userId: String, val email: String)
@Serializable data class UserResponse(val userId: String, val email: String)
@Serializable data class ErrorResponse(val error: String)
@Serializable data class GameSummary(
    val id: String,
    val result: String,
    val pgn: String,
    val createdAt: String,
)

fun Application.configureAuth() {
    val userRepo by inject<UserRepository>()
    val gameRepo by inject<GameRepository>()

    routing {
        post("/auth/register") {
            val req = call.receive<RegisterRequest>()
            if (req.email.isBlank() || req.password.length < 8) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid email or password (min 8 chars)"))
                return@post
            }
            val user = userRepo.register(req.email, req.password)
                ?: run {
                    call.respond(HttpStatusCode.Conflict, ErrorResponse("Email already registered"))
                    return@post
                }
            call.respond(AuthResponse(JwtConfig.makeToken(user.id, user.email), user.id.toString(), user.email))
        }

        post("/auth/login") {
            val req = call.receive<LoginRequest>()
            val user = userRepo.login(req.email, req.password)
                ?: run {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid credentials"))
                    return@post
                }
            call.respond(AuthResponse(JwtConfig.makeToken(user.id, user.email), user.id.toString(), user.email))
        }

        post("/auth/refresh") {
            val bearer = call.request.headers["Authorization"]
                ?.removePrefix("Bearer ")
                ?.trim()
                ?: run { call.respond(HttpStatusCode.Unauthorized, ErrorResponse("No token provided")); return@post }
            val decoded = runCatching { JwtConfig.verifier.verify(bearer) }.getOrNull()
                ?: run { call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid or expired token")); return@post }
            val userId = UUID.fromString(decoded.getClaim("userId").asString())
            val email = decoded.getClaim("email").asString()
            call.respond(AuthResponse(JwtConfig.makeToken(userId, email), userId.toString(), email))
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = UUID.fromString(principal.payload.getClaim("userId").asString())
                val user = userRepo.findById(userId)
                    ?: run { call.respond(HttpStatusCode.NotFound); return@get }
                call.respond(UserResponse(user.id.toString(), user.email))
            }

            get("/users/{id}/games") {
                val userId = call.parameters["id"]
                    ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: run {
                        call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid user ID"))
                        return@get
                    }
                val games = gameRepo.listForUser(userId)
                call.respond(games.map { GameSummary(it.id.toString(), it.result, it.pgn, it.createdAt.toString()) })
            }
        }
    }
}
