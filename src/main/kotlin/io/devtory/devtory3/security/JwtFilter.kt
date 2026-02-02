package io.devtory.devtory3.security

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SecurityException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException


@Component
class JwtFilter(
    private val tokenProvider: JwtTokenProvider) : OncePerRequestFilter()
{
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = resolveToken(request)
        if (StringUtils.hasText(jwt)) {
            try {
                val authentication: Authentication? = tokenProvider.getAuthentication(jwt)
                SecurityContextHolder.getContext().authentication = authentication
                if (authentication != null) {
                    LOGGER.info("Authenticated user: {}, uri: {}", authentication.name, request.requestURI)
                }
            } catch (e: SecurityException) {
                LOGGER.warn("Invalid JWT signature, uri: {}", request.requestURI)
                throw e // 인증 실패 시 요청 처리 중단
            } catch (e: MalformedJwtException) {
                LOGGER.warn("Invalid JWT signature, uri: {}", request.requestURI)
                throw e
            } catch (e: ExpiredJwtException) {
                LOGGER.warn("Expired JWT token, uri: {}", request.requestURI)
                throw e // 인증 실패 시 요청 처리 중단
            } catch (e: UnsupportedJwtException) {
                LOGGER.warn("Unsupported JWT token, uri: {}", request.requestURI)
                throw e // 인증 실패 시 요청 처리 중단
            } catch (e: IllegalArgumentException) {
                LOGGER.warn("JWT token compact of handler are invalid, uri: {}", request.requestURI)
                throw e // 인증 실패 시 요청 처리 중단
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else null
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JwtFilter::class.java)
        const val AUTHORIZATION_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}