package org.cardgame.dto

data class GameStateResponse(
    val id: Long,
    val status: GameSessionStatus,
    val players: List<PlayerState>,
    val currentPlayerId: Long,
    val remainingCards: Int,
    val winnerId: Long? = null
)

data class PlayerState(
    val userId: Long,
    val name: String,
    val score: Int
)