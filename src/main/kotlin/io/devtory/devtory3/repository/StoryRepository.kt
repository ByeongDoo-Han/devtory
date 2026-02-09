package io.devtory.devtory3.repository

import io.devtory.devtory3.entity.StoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<StoryEntity, Long> {
    @Query(
        """
        SELECT s FROM StoryEntity s
        JOIN FETCH s.author a
        WHERE (:cursor IS NULL OR s.id < :cursor)
        ORDER BY s.id DESC
        """
    )
    fun findNextPage(cursor: Long?, size: Int): List<StoryEntity>
}