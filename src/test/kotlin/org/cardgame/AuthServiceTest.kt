package org.cardgame

import org.cardgame.dto.User
import org.cardgame.repository.UserRepository
import org.cardgame.service.AuthService
import org.cardgame.util.JwtUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest {
    private val userRepository = mock(UserRepository::class.java)
    private val passwordEncoder = mock(PasswordEncoder::class.java)
    private val jwtUtils = mock(JwtUtils::class.java)
    private val authService = AuthService(userRepository, passwordEncoder, jwtUtils)

    private val testUser = User(
        id = 1,
        login = "testUser",
        passwordHash = "hashedPassword",
        name = "Test User"
    )

    @Test
    fun `should register new user`() {
        val expectedUser = User(
            login = "newUser",
            passwordHash = "hashed",
            name = "Test User"
        )

        `when`(userRepository.findByLogin("newUser")).thenReturn(null)
        `when`(passwordEncoder.encode("password")).thenReturn("hashed")
        `when`(userRepository.save(any())).thenReturn(expectedUser)

        val result = authService.register("newUser", "password", "Test User")

        assertEquals("newUser", result.login)
        verify(userRepository).save(any())
    }

    @Test
    fun `should throw when registering existing user`() {
        `when`(userRepository.findByLogin("existing")).thenReturn(testUser)
        assertThrows<IllegalArgumentException> {
            authService.register("existing", "pass", "Name")
        }
    }

    @Test
    fun `should authenticate valid user`() {
        val testUser = User(
            id = 1,
            login = "testUser",
            passwordHash = "hashedPassword",
            name = "Test User"
        )

        whenever(userRepository.findByLogin("testUser")).thenReturn(testUser)
        whenever(passwordEncoder.matches("password", "hashedPassword")).thenReturn(true)
        whenever(jwtUtils.generateToken(testUser)).thenReturn("token")

        // Test with correct password
        assertEquals("token", authService.authenticate("testUser", "password"))
    }

    @Test
    fun `should reject invalid credentials`() {
        `when`(userRepository.findByLogin("unknown")).thenReturn(null)
        assertThrows<BadCredentialsException> {
            authService.authenticate("unknown", "pass")
        }
    }
}