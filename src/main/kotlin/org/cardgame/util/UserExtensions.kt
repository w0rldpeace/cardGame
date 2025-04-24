package org.cardgame.util

import org.cardgame.dto.User
import org.cardgame.dto.UserResponse

fun User.toResponse() = UserResponse(
    id = id,
    login = login,
    name = name,
    createdAt = createdAt
)
