package org.cardgame.dto

import java.time.LocalDateTime

data class AuthResponse(
    val token: String
)

data class UserResponse(
    val id: Long,
    val login: String,
    val name: String,
    val createdAt: LocalDateTime
)