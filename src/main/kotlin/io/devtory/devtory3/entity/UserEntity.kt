package io.devtory.devtory3.entity

import io.devtory.devtory3.domain.User
import io.devtory.devtory3.domain.UserRole
import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Entity
@Table(
    name = "users",
    indexes = [
        Index(name = "idx_email", columnList = "email"),
        Index(name = "idx_nickname", columnList = "nickname")
    ]
)
class UserEntity(
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    val id: String = UUID.randomUUID().toString(),

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    var nickname: String,

    @Column(name = "email", nullable = false, unique = true, length = 255)
    val email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: UserRoleEntity = UserRoleEntity.USER,

    @Column(name = "profile_image_url", length = 500)
    var profileImageUrl: String? = null,

    @Column(name = "last_story_created_at")
    var lastStoryCreatedAt: Instant? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
){
    fun toDomain():User{
        return User(
            id = id,
            nickname = nickname,
            email = email,
            passwordHash = passwordHash,
            role = when(role){
                UserRoleEntity.USER -> UserRole.USER
                UserRoleEntity.ADMIN -> UserRole.ADMIN
            },
            profileImageUrl = profileImageUrl,
            lastStoryCreatedAt = LocalDateTime.ofInstant(lastStoryCreatedAt, ZoneOffset.UTC),
            createdAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC),
            updatedAt = LocalDateTime.ofInstant(updatedAt, ZoneOffset.UTC)
        )
    }
    companion object{
        fun forInsert(
            nickname: String,
            passwordHash: String,
            email: String,
        ): UserEntity = UserEntity(
            email = email,
            nickname = nickname,
            passwordHash = passwordHash,
        )
    }
}

enum class UserRoleEntity {
    USER,
    ADMIN
}