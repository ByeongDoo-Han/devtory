package io.devtory.devtory3.repository

import io.devtory.devtory3.entity.StoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryRepository : JpaRepository<StoryEntity, Long> {
}