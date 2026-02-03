package io.devtory.devtory3.util

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serial
import java.io.Serializable

class UserDetails(
    private val email: String,
    private val authority: Collection<GrantedAuthority?>,
) : UserDetails, Serializable{
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authority
    }

    override fun getPassword(): String? {
        return null
    }

    override fun getUsername(): String {
        return email
    }

    companion object {
        @Serial
        private val serialVersionUID = -8833032179672425737L
    }
}