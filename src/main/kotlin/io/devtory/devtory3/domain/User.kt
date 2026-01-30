package io.devtory.devtory3.domain

import java.time.LocalDateTime

data class User(
    val nickname: String,
    val email: String,
    val passwordHash: String,
    val profileImageUrl: String? = null,
    val role: UserRole,
    val lastStoryCreatedAt:LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            nickname: String,
            passwordHash: String,
            email: String
        ): User {
            return User(
                nickname = nickname,
                email = email,
                role = UserRole.USER,
                passwordHash = passwordHash,
                lastStoryCreatedAt = null,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
}

enum class UserRole {
    USER,
    ADMIN
}