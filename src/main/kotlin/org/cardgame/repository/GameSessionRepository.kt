package org.cardgame.repository

import org.cardgame.dto.GameSession
import org.springframework.data.jpa.repository.JpaRepository

interface GameSessionRepository : JpaRepository<GameSession, Long>