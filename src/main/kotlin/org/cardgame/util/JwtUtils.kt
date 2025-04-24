package org.cardgame.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.cardgame.dto.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtUtils(
    @Value("\${app.jwtSecret}") private val jwtSecret: String,
    @Value("\${app.jwtExpirationMs}") private val jwtExpirationMs: Int
) {
    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray(Charsets.UTF_8))
    }

    fun generateToken(user: User): String {
        return Jwts.builder()
            .subject(user.login)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(key)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getUserNameFromToken(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }
}