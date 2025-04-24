package org.cardgame.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.cardgame.dto.Card
import org.cardgame.dto.GameSession
import org.cardgame.dto.GameStateResponse
import org.cardgame.dto.Participation
import org.cardgame.dto.PlayerState

fun GameSession.toResponse(
    participations: List<Participation>,
    objectMapper: ObjectMapper
): GameStateResponse {
    return GameStateResponse(
        id = id,
        status = status,
        players = participations.map { it.toPlayerState() },
        currentPlayerId = participations.getOrNull(currentPlayerIndex)?.user?.id ?: -1,
        remainingCards = objectMapper.readValue(deck, Array<Card>::class.java).size,
        winnerId = participations.maxByOrNull { it.score }?.takeIf { it.score >= 30 }?.user?.id
    )
}

fun Participation.toPlayerState() = PlayerState(
    userId = user.id,
    name = user.name,
    score = score
)