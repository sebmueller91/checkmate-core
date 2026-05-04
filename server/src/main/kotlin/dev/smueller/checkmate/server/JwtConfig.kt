package dev.smueller.checkmate.server

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import java.util.UUID

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: "dev-secret-do-not-use-in-production"
    const val ISSUER = "checkmate"
    const val AUDIENCE = "checkmate-users"
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)

    val verifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun makeToken(userId: UUID, email: String): String = JWT.create()
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .withClaim("userId", userId.toString())
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
        .sign(algorithm)
}
