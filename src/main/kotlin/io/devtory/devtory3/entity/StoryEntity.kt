package io.devtory.devtory3.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(
    name = "stories",
    indexes = [
        Index(name = "idx_slug", columnList = "slug", unique = true),
        Index(name = "idx_author", columnList = "author_id")
    ]
)
class StoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    val slug: String? = null,

    @Column(name= "title", nullable = false, length = 100)
    val title: String? = null,

    @Column(name= "content", nullable = false, columnDefinition = "TEXT")
    val content: String? = null,

    @Column(name = "view_count", nullable = false)
    var viewCount: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    var visibility: VisibilityEntity = VisibilityEntity.DRAFT,

    @Column(name = "like_count", nullable = false)
    var likeCount: Long = 0,

    @Column(name = "thumbnail_url", length = 500)
    val thumbnailUrl: String? = null,

    @ManyToOne
    val author: UserEntity? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)

enum class VisibilityEntity {
    PUBLIC,
    PRIVATE,
    DRAFT
}