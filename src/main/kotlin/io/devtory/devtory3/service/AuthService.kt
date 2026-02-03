package io.devtory.devtory3.service

import io.devtory.devtory3.domain.User
import io.devtory.devtory3.dto.*
import io.devtory.devtory3.entity.UserEntity
import io.devtory.devtory3.exception.*
import io.devtory.devtory3.repository.UserRepository
import io.devtory.devtory3.security.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestBody

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val redisService: RedisService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val LOGGER = LoggerFactory.getLogger(javaClass)

    /**
     * 회원가입
     */
    fun register(request: RegisterRequest): RegisterResponse {
        val email = request.email
        val nickname = request.nickname

        // 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw CustomException(ErrorCode.USER_ALREADY_EXISTED)
        }
        if (userRepository.existsByNickname(nickname)) {
            throw CustomException(ErrorCode.NICKNAME_ALREADY_EXISTED)
        }

        // 비밀번호 해싱
        val passwordHash = passwordEncoder.encode(request.password)

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

    @Transactional
    suspend fun login(request: LoginRequest): TokenResponse {
        // 사용자 조회
        val userEntity = withContext(Dispatchers.IO) {
            userRepository.findByEmail(request.email)
        } ?:throw CustomException(ErrorCode.USER_NOT_FOUND)

        // 비밀번호 검증
        if(!passwordEncoder.matches(request.password, userEntity.passwordHash)){
            throw CustomException(ErrorCode.PASSWORD_MISMATCH)
        }

        // 인증정보 생성
        val role = ROLE + userEntity.role
        val authentication = UsernamePasswordAuthenticationToken(
            userEntity.email,
            null,
            listOf(SimpleGrantedAuthority(role))
        )

        // 인증정보 통한 토큰 발급
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken(authentication)

        // redis에 refresh token 저장
        redisService.saveRefreshToken(userEntity.email, refreshToken)

        // 토큰 반환
        return TokenResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    suspend fun logout(request: HttpServletRequest) {
        val authHeader: String? = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader == null || !authHeader.startsWith(GRANT_TYPE)) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }
        val token = authHeader.substring(7)
        val email = jwtTokenProvider.getAuthentication(token)?.name.orEmpty()
        redisService.deleteRefreshToken(email)
    }

    @Transactional
    suspend fun refresh(request: HttpServletRequest): TokenResponse {
        val authHeader: String? = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader == null || !authHeader.startsWith(GRANT_TYPE)) {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        val refreshToken = authHeader.substring(7)
        if(jwtTokenProvider.getTokenType(refreshToken)!= REFRESH){
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        val email = jwtTokenProvider.getAuthentication(refreshToken)?.name.orEmpty()

        // redis에 저장된 refresh token과 비교
        if(!redisService.getRefreshToken(email).equals(refreshToken)){
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        }

        // 기존 refresh token 제거
        redisService.deleteRefreshToken(email)

        // 새로운 인증정보
        val role = ROLE_USER
        val authentication = UsernamePasswordAuthenticationToken(
            email,
            null,
            listOf(SimpleGrantedAuthority(role))
        )

        // 인증정보 통한 토큰 발급
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val newRefreshToken = jwtTokenProvider.createRefreshToken(authentication)

        // redis에 refresh token 저장
        redisService.saveRefreshToken(email, newRefreshToken)

        // 토큰 반환
        return TokenResponse(
            accessToken = accessToken,
            refreshToken = newRefreshToken
        )
    }

    companion object{
        const val AUTHORIZATION_HEADER = "Authorization"
        private const val GRANT_TYPE = "Bearer "
        private const val ROLE_USER = "ROLE_USER"
        private const val ROLE = "ROLE_"
        private const val REFRESH = "refresh"
    }
}
