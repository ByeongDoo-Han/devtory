package io.devtory.devtory3.controller

import io.devtory.devtory3.dto.*
import io.devtory.devtory3.service.AuthService
import io.devtory.devtory3.util.UserDetails
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    @Value("\${jwt.refresh-token-expiration}") private val REFRESH_TOKEN_EXPIRATION: Long
) {

    /**
     * 회원가입
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): RegisterResponse {
        return authService.register(request)
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    suspend fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.login(request)
        val refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(REFRESH_TOKEN_EXPIRATION) // 7 days
            .sameSite("Strict")
            .build()
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(TokenResponse(
                accessToken = tokenResponse.accessToken,
                refreshToken = null
            ))
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    suspend fun refresh(request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<TokenResponse> {
        val tokenResponse = authService.refresh(request)

        val refreshCookie = ResponseCookie.from("refreshToken", tokenResponse.refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .sameSite("Strict")
            .build()
        response.addHeader("refreshToken", tokenResponse.refreshToken)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
            .body(TokenResponse(
                accessToken = tokenResponse.accessToken,
                refreshToken = null,
            ))
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    suspend fun logout(request: HttpServletRequest, response:HttpServletResponse):ResponseEntity<Void> {
        authService.logout(request)
        val deleteCookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days
            .sameSite("Strict")
            .build()
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString()).build()
    }
}