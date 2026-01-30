package io.devtory.devtory3.service

import io.devtory.devtory3.security.PasswordEncoder
import io.devtory.devtory3.domain.User
import io.devtory.devtory3.dto.*
import io.devtory.devtory3.entity.UserEntity
import io.devtory.devtory3.exception.DuplicateEmailException
import io.devtory.devtory3.exception.DuplicateNicknameException
import io.devtory.devtory3.repository.UserRepository
import io.devtory.devtory3.security.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val redisService: RedisService,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 회원가입
     */
    fun register(request: RegisterRequest): RegisterResponse {
        val email = request.email
        val nickname = request.nickname

        // 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw DuplicateEmailException(request.email)
        }
        if (userRepository.existsByNickname(nickname)) {
            throw DuplicateNicknameException(request.nickname)
        }

        // 비밀번호 해싱
        val passwordHash = PasswordEncoder.encode(request.password)

        // 사용자 생성
        val user = User.create(
            email = email,
            nickname = nickname,
            passwordHash = passwordHash,
        )

        // UUID 생성
        val userId = java.util.UUID.randomUUID().toString()

        // 새 엔티티 생성
        val entityToSave = UserEntity.forInsert(
            id = userId,
            email = user.email,
            nickname = user.nickname,
            passwordHash = user.passwordHash,
        )

        // 저장
        userRepository.save(entityToSave)

        return RegisterResponse(
            id = userId,
            nickname = user.nickname,
            email = user.email
        )
    }

    fun login(request: LoginRequest): TokenResponse {
        TODO("Not yet implemented")
    }

    fun refreshToken(request: RefreshTokenRequest): TokenResponse {
        TODO("Not yet implemented")
    }
}
