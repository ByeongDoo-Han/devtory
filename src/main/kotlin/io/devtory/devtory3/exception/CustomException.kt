package io.devtory.devtory3.exception

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException()