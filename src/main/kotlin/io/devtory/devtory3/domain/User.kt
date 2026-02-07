package io.devtory.devtory3.domain

import java.time.LocalDateTime

data class User(
    val id: String? = null,
    val nickname: String,
    val email: String,
    val passwordHash: String,
    val profileImageUrl: String? = null,
    val role: UserRole,
    val lastStoryCreatedAt:LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    init{
        require(nickname.length in 2..10) { "Nickname must be between 2 and 10 characters." }

    }
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
            )
        }
    }
}

enum class UserRole {
    USER,
    ADMIN
}