package org.cardgame.dto

data class RegisterRequest(
    val login: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val login: String,
    val password: String
)