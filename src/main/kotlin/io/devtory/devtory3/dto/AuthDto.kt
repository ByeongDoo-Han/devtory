package io.devtory.devtory3.dto

/**
 * 회원가입 요청
 */
data class RegisterRequest(
    val nickname: String,
    val email: String,
    val password: String
)

/**
 * 회원가입 응답
 */
data class RegisterResponse(
    val id: String,
    val nickname: String,
    val email: String
)

/**
 * 로그인 요청
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * 토큰 응답
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

/**
 * Refresh Token 요청
 */
data class RefreshTokenRequest(
    val refreshToken: String
)