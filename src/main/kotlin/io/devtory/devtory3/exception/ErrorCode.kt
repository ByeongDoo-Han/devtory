package io.devtory.devtory3.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(httpStatus: HttpStatus, errorCode: String, errormessage: String){

    // auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-001", "Invalid token."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "Expired token."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "Unsupported token."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "Wrong token."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-005", "Access denied."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "AUTH-006", "Password mismatch."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-007", "User not found."),

    USER_ALREADY_EXISTED(HttpStatus.CONFLICT, "AUTH-008", "User already existed."),
    NICKNAME_ALREADY_EXISTED(HttpStatus.CONFLICT, "AUTH-009", "Nickname already existed."),
    ;

    val status: HttpStatus = httpStatus
    val code: String = errorCode
    val message: String = errormessage
}