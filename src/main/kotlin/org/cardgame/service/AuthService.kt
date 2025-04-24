package org.cardgame.service

import org.cardgame.dto.User
import org.cardgame.repository.UserRepository
import org.cardgame.util.JwtUtils
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
) {
    fun register(login: String, password: String, name: String): User {
        require(userRepository.findByLogin(login) == null) { "User already exists" }

        return userRepository.save(
            User(
                login = login,
                passwordHash = passwordEncoder.encode(password),
                name = name
            )
        )
    }

    fun authenticate(login: String, password: String): String {
        val user = userRepository.findByLogin(login) ?: throw BadCredentialsException("Invalid credentials")
        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw BadCredentialsException("Invalid credentials")
        }
        return jwtUtils.generateToken(user)
    }
}