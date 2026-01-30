package io.devtory.devtory3.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

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
    val id: String?,

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    val nickname: String?,

    @Column(name = "email", nullable = false, unique = true, length = 255)
    val email: String?,

    @Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    var role: UserRoleEntity = UserRoleEntity.USER,

    @Column(name = "profile_image_url", length = 500)
    val profileImageUrl: String? = null,

    @Column(name = "last_story_created_at")
    var lastStoryCreatedAt: LocalDate? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
){
    companion object{
        fun forInsert(
            id: String,
            nickname: String,
            passwordHash: String,
            email: String,
        ): UserEntity = UserEntity(
            id = id,
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