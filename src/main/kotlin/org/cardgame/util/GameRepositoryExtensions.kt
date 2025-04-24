package org.cardgame.util

import org.cardgame.dto.GameSession
import org.cardgame.repository.GameSessionRepository

fun GameSessionRepository.findByIdOrThrow(id: Long): GameSession {
    return findById(id).orElseThrow {
        NoSuchElementException("Game session $id not found")
    }
}