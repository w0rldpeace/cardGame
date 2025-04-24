package org.cardgame.controller

import jakarta.validation.Valid
import org.cardgame.dto.AuthResponse
import org.cardgame.dto.LoginRequest
import org.cardgame.dto.RegisterRequest
import org.cardgame.dto.UserResponse
import org.cardgame.service.AuthService
import org.cardgame.util.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = authService.register(request.login, request.password, request.name)
        return ResponseEntity.ok(user.toResponse())
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val token = authService.authenticate(request.login, request.password)
        return ResponseEntity.ok(AuthResponse(token))
    }
}