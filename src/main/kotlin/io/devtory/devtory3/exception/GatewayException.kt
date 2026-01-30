package io.devtory.devtory3.exception

/**
 * 인증 실패 예외
 */
class AuthenticationFailedException(message: String) : RuntimeException(message)

/**
 * 중복 이메일 예외
 */
class DuplicateEmailException(email: String) : RuntimeException("이미 사용 중인 이메일입니다: $email")

/**
 * 중복 닉네임 예외
 */
class DuplicateNicknameException(nickname: String) : RuntimeException("이미 사용 중인 닉네임입니다: $nickname")

/**
 * 사용자를 찾을 수 없음
 */
class UserNotFoundException(identifier: String) : RuntimeException("사용자를 찾을 수 없습니다: $identifier")
