package io.devtory.devtory3.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증
 */
@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}") private val refreshTokenExpiration: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKey.toByteArray())

    /**
     * Access Token 생성
     */
    fun createAccessToken(userId: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpiration)

        return Jwts.builder()
            .subject(userId)
            .claim("role", role)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * Refresh Token 생성
     */
    fun createRefreshToken(userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpiration)

        return Jwts.builder()
            .subject(userId)
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    fun getUserIdFromToken(token: String): String {
        return getClaims(token).subject
    }

    /**
     * 토큰에서 Role 추출
     */
    fun getRoleFromToken(token: String): String? {
        return getClaims(token).get("role", String::class.java)
    }

    /**
     * 토큰 검증
     */
    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 토큰 타입 확인 (access/refresh)
     */
    fun getTokenType(token: String): String? {
        return try {
            getClaims(token).get("type", String::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
