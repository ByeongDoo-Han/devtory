package io.devtory.devtory3.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    /**
     * 값 저장 (TTL 포함)
     */
    suspend fun set(key: String, value: String, ttl: Duration) = withContext(Dispatchers.IO) {
        redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS)
    }

    /**
     * 값 조회
     */
    suspend fun get(key: String): String? = withContext(Dispatchers.IO) {
        redisTemplate.opsForValue().get(key)
    }

    /**
     * 값 삭제
     */
    suspend fun delete(key: String) = withContext(Dispatchers.IO) {
        redisTemplate.delete(key)
    }

    /**
     * Refresh Token 저장 (7일 TTL)
     */
    suspend fun saveRefreshToken(userId: String, token: String) {
        set("refresh_token:$userId", token, Duration.ofDays(7))
    }

    /**
     * Refresh Token 조회
     */
    suspend fun getRefreshToken(userId: String): String? {
        return get("refresh_token:$userId")
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     */
    suspend fun deleteRefreshToken(userId: String) {
        delete("refresh_token:$userId")
    }
}