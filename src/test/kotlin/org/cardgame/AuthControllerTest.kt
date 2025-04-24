package org.cardgame

import org.cardgame.controller.AuthController
import org.cardgame.dto.*
import org.cardgame.service.AuthService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus

class AuthControllerTest {
    private val authService = mock(AuthService::class.java)
    private val controller = AuthController(authService)

    @Test
    fun `should register user`() {
        val request = RegisterRequest("new", "pass", "Name")
        val user = User(login = "new", passwordHash = "hash", name = "Name")
        `when`(authService.register("new", "pass", "Name")).thenReturn(user)

        val response = controller.register(request)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("new", response.body?.login)
    }
}