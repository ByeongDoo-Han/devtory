package io.devtory.devtory3.controller

import io.devtory.devtory3.dto.*
import io.devtory.devtory3.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
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
    suspend fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): TokenResponse {
        val tokenResponse = authService.login(request)
        val cookie = Cookie("refreshToken", tokenResponse.refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 7 * 24 * 60 * 60 // 7 days
        }
        response.addCookie(cookie)
        return authService.login(request)
    }

    /**
     * 토큰 갱신
     */
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    suspend fun refresh(@AuthenticationPrincipal @Valid @RequestBody request: RefreshTokenRequest, response: HttpServletResponse): TokenResponse {
        val tokenResponse = authService.refreshToken(request)
        val cookie = Cookie("refreshToken", tokenResponse.refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/"
            maxAge = 7 * 24 * 60 * 60 // 7 days
        }
        response.addCookie(cookie)
        return tokenResponse
    }
}