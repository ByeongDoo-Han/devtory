package io.devtory.devtory3.service

import io.devtory.devtory3.domain.User
import io.devtory.devtory3.dto.*
import io.devtory.devtory3.entity.UserEntity
import io.devtory.devtory3.exception.DuplicateEmailException
import io.devtory.devtory3.exception.DuplicateNicknameException
import io.devtory.devtory3.exception.UserNotFoundException
import io.devtory.devtory3.repository.UserRepository
import io.devtory.devtory3.security.JwtTokenProvider
import io.devtory.devtory3.security.PasswordEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

        // 새 엔티티 생성
        val entityToSave = UserEntity.forInsert(
            email = user.email,
            nickname = user.nickname,
            passwordHash = user.passwordHash,
        )

        // 저장
        val savedUser = userRepository.save(entityToSave)

        return RegisterResponse(
            id = savedUser.id,
            nickname = savedUser.nickname,
            email = savedUser.email
        )
    }

    suspend fun login(request: LoginRequest): TokenResponse {
        val userEntity = withContext(Dispatchers.IO) {
            userRepository.findByEmail(request.email)
        } ?:throw UserNotFoundException(request.email)

        val accessToken = jwtTokenProvider.createAccessToken(userEntity.id, userEntity.email, userEntity.role.name)
        val refreshToken = jwtTokenProvider.createRefreshToken(userEntity.id, userEntity.email, userEntity.role.name)

        redisService.saveRefreshToken(userEntity.id, refreshToken)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userEntity.id
        )
    }

    suspend fun refreshToken(request: RefreshTokenRequest): TokenResponse {
        redisService.deleteRefreshToken(request.refreshToken)
        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val email = jwtTokenProvider.getEmailFromToken(request.refreshToken)
        val role = jwtTokenProvider.getRoleFromToken(request.refreshToken)

        val accessToken = jwtTokenProvider.createAccessToken(userId,email,role)
        val refreshToken = jwtTokenProvider.createRefreshToken(userId,email,role)

        redisService.saveRefreshToken(userId, refreshToken)

        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId
        )
    }
}
