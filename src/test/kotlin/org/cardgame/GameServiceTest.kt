package org.cardgame

import com.fasterxml.jackson.databind.ObjectMapper
import org.cardgame.dto.*
import org.cardgame.repository.*
import org.cardgame.service.GameService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.util.*

class GameServiceTest {
    private val gameRepo: GameSessionRepository = mock()
    private val participationRepo: ParticipationRepository = mock()
    private val turnRepo: TurnRepository = mock()
    private val objectMapper = ObjectMapper()
    private val service = GameService(participationRepo, gameRepo, turnRepo, objectMapper)

    private lateinit var testGame: GameSession
    private lateinit var testUser: User

    @BeforeEach
    fun setup() {
        testUser = User(
            id = 1,
            login = "testUser",
            passwordHash = "hashedPassword",
            name = "Test User"
        )

        testGame = GameSession(
            id = 1,
            createdBy = testUser,
            status = GameSessionStatus.WAIT_FOR_PLAYERS
        )

        // Proper mock setup with explicit types
        whenever(gameRepo.findById(eq(1))).thenReturn(Optional.of(testGame))
        whenever(gameRepo.save(any<GameSession>())).thenAnswer { it.arguments[0] }
        whenever(participationRepo.save(any<Participation>())).thenAnswer { it.arguments[0] }
    }

    @Test
    fun `should create game`() {
        whenever(participationRepo.save(any<Participation>())).thenAnswer { it.arguments[0] }

        val game = service.createGame(testUser)

        assertEquals(testUser.id, game.createdBy.id)
        verify(participationRepo).save(any<Participation>())
        verify(gameRepo).save(any<GameSession>())
    }

    @Test
    fun `should allow joining game`() {
        val newUser = User(login = "new", passwordHash = "hash", name = "New")
        whenever(participationRepo.countByGameSession(testGame)).thenReturn(1)
        whenever(participationRepo.save(any<Participation>())).thenAnswer { it.arguments[0] }

        service.joinGame(1, newUser)
        verify(participationRepo).save(any<Participation>())
    }

    @Test
    fun `should process points card turn`() {
        testGame.status = GameSessionStatus.IN_PROGRESS
        testGame.deck = """[{"type":"POINTS","name":"Test","value":5}]"""

        val participation = Participation(
            id = 1,
            gameSession = testGame,
            user = testUser,
            score = 0
        )

        whenever(participationRepo.findByGameSession(testGame))
            .thenReturn(listOf(participation))
        whenever(participationRepo.save(any<Participation>()))
            .thenAnswer { it.arguments[0] }

        service.processTurn(1, testUser)

        assertEquals(5, participation.score)
        verify(participationRepo).save(any<Participation>())
    }

    @Test
    fun `should process steal action card`() {
        testGame.status = GameSessionStatus.IN_PROGRESS
        testGame.deck = """[{"type":"ACTION","name":"Steal","value":2,"effectType":"STEAL"}]"""

        val current = Participation(
            id = 1,
            gameSession = testGame,
            user = testUser,
            score = 0
        )
        val target = Participation(
            id = 2,
            gameSession = testGame,
            user = User(2, "target", "hash", "Target"),
            score = 5
        )

        whenever(participationRepo.findByGameSession(testGame))
            .thenReturn(listOf(current, target))
        whenever(participationRepo.saveAll(any<List<Participation>>()))
            .thenAnswer { it.arguments[0] as List<Participation> }

        service.processTurn(1, testUser)

        assertEquals(2, current.score)
        assertEquals(3, target.score)
    }
}