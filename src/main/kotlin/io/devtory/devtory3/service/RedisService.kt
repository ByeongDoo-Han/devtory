package io.devtory.devtory3.service

import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisService(
    private val redissonClient: RedissonClient,
) {

    /**
     * Refresh Token 저장 (7일 TTL)
     */
    suspend fun saveRefreshToken(email: String, token: String) {
        val bucket = redissonClient.getBucket<String>(REFRESH_TOKEN_PREFIX + email)
        bucket[token] = Duration.ofDays(7)
    }

    /**
     * Refresh Token 조회
     */
    suspend fun getRefreshToken(userId: String): String? {
        val bucket =
            redissonClient.getBucket<String>(REFRESH_TOKEN_PREFIX + userId)
        return bucket.get()
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     */
    suspend fun deleteRefreshToken(email: String) {
        val bucket =
            redissonClient.getBucket<String>(REFRESH_TOKEN_PREFIX + email)
        bucket.delete()
    }

    companion object {
        private const val REFRESH_TOKEN_PREFIX = "refresh_token:"
    }
}