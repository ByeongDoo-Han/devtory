package io.devtory.devtory3.security

import io.devtory.devtory3.util.UserDetails
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*
import javax.crypto.SecretKey

/**
 * JWT 토큰 생성 및 검증
 */
@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val SECRET_KEY: String,
    @Value("\${jwt.access-token-expiration}") private val ACCESS_TOKEN_EXPIRATION: Long,
    @Value("\${jwt.refresh-token-expiration}") private val REFRESH_TOKEN_EXPIRATION: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(SECRET_KEY.toByteArray())

    /**
     * Access Token 생성
     */
    fun createAccessToken(authentication : Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = Date()
        val expiryDate = Date(now.time + ACCESS_TOKEN_EXPIRATION)

        return Jwts.builder()
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .claim(TOKEN_TYPE, ACCESS)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    /**
     * Refresh Token 생성
     */
    fun createRefreshToken(authentication:Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = Date()
        val expiryDate = Date(now.time + REFRESH_TOKEN_EXPIRATION)

        return Jwts.builder()
            .subject(authentication.name)
            .claim(AUTHORITIES_KEY, authorities)
            .claim(TOKEN_TYPE, REFRESH)
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
     * 토큰에서 사용자 email 추출
     */
    fun getEmailFromToken(token: String): String {
        return getClaims(token).get("email", String::class.java)
    }

    /**
     * 토큰에서 Role 추출
     */
    fun getRoleFromToken(token: String): String {
        return getClaims(token).get("role", String::class.java)
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

    // Token에 담겨있는 정보를 이용해 Authentication 객체를 리턴
    fun getAuthentication(token: String?): Authentication? {
        val claims: Claims = getClaims(token!!)
        val authorities: Collection<GrantedAuthority?> = Arrays.stream(
            claims[AUTHORITIES_KEY].toString().split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray())
            .filter { str: String? ->
                StringUtils.hasText(str)
            }
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }.toList()
        val userDetail = UserDetails(claims.subject, authorities)
        return UsernamePasswordAuthenticationToken(userDetail, token, authorities)
    }

    // 토큰의 유효성 검증, 토큰을 파싱하여 exception들을 캐치
    fun validateToken(token: String?): Boolean {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            return true
        } catch (e: SecurityException) {
            LOGGER.info("잘못된 JWT 서명입니다.")
        } catch (e: MalformedJwtException) {
            LOGGER.info("잘못된 JWT 서명입니다.")
        } catch (e: ExpiredJwtException) {
            LOGGER.info("만료된 JWT 토큰입니다.")
        } catch (e: UnsupportedJwtException) {
            LOGGER.info("지원되지 않는 JWT 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            LOGGER.info("JWT 토큰이 잘못되었습니다.")
        }
        return false
    }

    companion object{
        private val LOGGER: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java.toString())
        const val AUTHORITIES_KEY: String = "auth"
        const val TOKEN_TYPE:String = "type"
        const val ACCESS:String = "access"
        const val REFRESH:String = "refresh"
        const val ROLE:String = "role"
    }
}
