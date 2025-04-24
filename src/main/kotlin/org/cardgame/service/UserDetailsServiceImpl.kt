package org.cardgame.service

import org.cardgame.dto.CustomUserDetails
import org.cardgame.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(login: String): CustomUserDetails {
        val user = userRepository.findByLogin(login)
            ?: throw UsernameNotFoundException("User not found with login: $login")

        return CustomUserDetails(user)
    }
}