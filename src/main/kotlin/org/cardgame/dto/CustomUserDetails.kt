package org.cardgame.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {

    override fun getAuthorities() = emptyList<GrantedAuthority>()
    override fun getPassword() = user.passwordHash
    override fun getUsername() = user.login
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true

    fun getUserEntity() = user
}