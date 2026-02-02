package io.devtory.devtory3.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(httpStatus: HttpStatus, errorCode: String, errormessage: String){

    // auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-001", "Invalid token."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "Expired token."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "Unsupported token."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "Wrong token."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-005", "Access denied.");

    val status: HttpStatus = httpStatus
    val code: String = errorCode
    val message: String = errormessage
}