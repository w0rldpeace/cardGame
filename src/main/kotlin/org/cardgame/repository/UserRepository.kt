package org.cardgame.repository

import org.cardgame.dto.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByLogin(login: String): User?
}