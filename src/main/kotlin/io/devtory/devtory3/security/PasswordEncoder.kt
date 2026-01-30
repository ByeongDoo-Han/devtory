package io.devtory.devtory3.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object PasswordEncoder {
    private val encoder = BCryptPasswordEncoder()

    fun encode(rawPassword: String): String {
        return encoder.encode(rawPassword)
    }

    fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }
}