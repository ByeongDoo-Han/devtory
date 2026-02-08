package io.devtory.devtory3.dto

data class CursorPage<T>(
    val content: List<T>,
    val hasNext: Boolean,
    val nextCursor: Long?
)
