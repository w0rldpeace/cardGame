package org.cardgame.repository

import org.cardgame.dto.GameSession
import org.cardgame.dto.Participation
import org.cardgame.dto.User
import org.springframework.data.jpa.repository.JpaRepository

interface ParticipationRepository : JpaRepository<Participation, Long> {
    fun findByGameSession(gameSession: GameSession): List<Participation>
    fun countByGameSession(gameSession: GameSession): Int
    fun findByGameSessionAndUser(gameSession: GameSession, user: User): Participation?
}