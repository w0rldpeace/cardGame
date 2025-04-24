package org.cardgame

import jakarta.transaction.Transactional
import org.cardgame.dto.*
import org.cardgame.service.GameService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import org.cardgame.dto.User
import org.cardgame.repository.GameSessionRepository
import org.cardgame.repository.UserRepository
import org.junit.jupiter.api.BeforeEach

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class GameIntegrationTest {
    @Autowired lateinit var gameService: GameService
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var gameSessionRepository: GameSessionRepository

    private lateinit var creator: User
    private lateinit var player: User

    @BeforeEach
    fun setup() {
        creator = userRepository.save(User(
            login = "creator_${System.currentTimeMillis()}",
            passwordHash = "hash",
            name = "Creator"
        ))

        player = userRepository.save(User(
            login = "player_${System.currentTimeMillis()}",
            passwordHash = "hash",
            name = "Player"
        ))
    }

    @Test
    fun `should complete full game flow`() {
        // Create game
        val game = gameService.createGame(creator)
        assertNotNull(game.id)

        // Join game
        gameService.joinGame(game.id, player)

        // Start game
        gameService.startGame(game.id, creator)

        // Verify
        val updatedGame = gameSessionRepository.findById(game.id).get()
        assertEquals(GameSessionStatus.IN_PROGRESS, updatedGame.status)
        assertTrue(updatedGame.deck.isNotEmpty())
    }
}