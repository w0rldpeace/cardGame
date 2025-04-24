package org.cardgame.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.cardgame.dto.Card
import org.cardgame.dto.CardType
import org.cardgame.dto.EffectType
import org.cardgame.dto.GameSession
import org.cardgame.dto.GameSessionStatus
import org.cardgame.dto.Participation
import org.cardgame.dto.Turn
import org.cardgame.dto.User
import org.cardgame.repository.GameSessionRepository
import org.cardgame.repository.ParticipationRepository
import org.cardgame.repository.TurnRepository
import org.cardgame.util.findByIdOrThrow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private const val WINNING_SCORE = 30

@Service
class GameService(
    private val participationRepository: ParticipationRepository,
    private val gameRepository: GameSessionRepository,
    private val turnRepository: TurnRepository,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createGame(creator: User): GameSession {
        val game = GameSession(
            createdBy = creator,
            status = GameSessionStatus.WAIT_FOR_PLAYERS
        ).also { gameRepository.save(it) }

        participationRepository.save(
            Participation(
                gameSession = game,
                user = creator,
                score = 0
            )
        )

        logger.info("Game ${game.id} created by ${creator.name}")
        return game
    }

    fun joinGame(gameId: Long, user: User) {
        val game = gameRepository.findByIdOrThrow(gameId)

        require(game.status == GameSessionStatus.WAIT_FOR_PLAYERS) {
            "Cannot join - game already started"
        }

        val participantCount = participationRepository.countByGameSession(game)
        require(participantCount < 4) {
            "Game is full (4/4 players)"
        }

        require(participationRepository.findByGameSessionAndUser(game, user) == null) {
            "User already in game"
        }

        participationRepository.save(
            Participation(
                gameSession = game,
                user = user,
                score = 0
            )
        )

        logger.info("User ${user.name} joined game $gameId")
    }

    fun getGame(gameId: Long): GameSession {
        return gameRepository.findByIdOrThrow(gameId)
    }

    fun startGame(gameId: Long, user: User) {
        val game = gameRepository.findByIdOrThrow(gameId)

        require(game.createdBy.id == user.id) {
            "Only game creator can start the game"
        }

        val participantCount = participationRepository.countByGameSession(game)
        require(participantCount >= 2) {
            "Need at least 2 players to start"
        }

        game.status = GameSessionStatus.IN_PROGRESS
        game.startedAt = LocalDateTime.now()
        initializeDeck(game)
        gameRepository.save(game)

        logger.info("Game $gameId started with $participantCount players")
    }

    fun processTurn(gameId: Long, user: User): GameSession {
        val game = gameRepository.findByIdOrThrow(gameId)
        validateGameState(game, user)

        val participants = participationRepository.findByGameSession(game).sortedBy { it.id }
        val currentParticipation = participants[game.currentPlayerIndex]

        val cards = getDeckCards(game.deck)
        val card = cards.removeFirst()
        game.deck = objectMapper.writeValueAsString(cards)

        when (card.type) {
            CardType.POINTS -> handlePointsCard(currentParticipation, card)
            CardType.ACTION -> handleActionCard(game, participants, currentParticipation, card)
        }

        if (currentParticipation.score >= WINNING_SCORE) {
            finishGame(game)
        }

        updateTurnOrder(game, participants)

        saveTurnHistory(game, user, card, currentParticipation.score)

        return gameRepository.save(game)
    }

    private fun handlePointsCard(participation: Participation, card: Card) {
        participation.score += card.value
        participationRepository.save(participation)
    }

    private fun handleActionCard(
        game: GameSession,
        participants: List<Participation>,
        current: Participation,
        card: Card
    ) {
        when (card.effectType) {
            EffectType.BLOCK -> {
                game.nextPlayerSkip = true
                logger.info("Player ${current.user.name} used Block card")
            }

            EffectType.STEAL -> {
                val target = participants.filter { it != current }.random()
                val stolen = minOf(card.value, target.score)
                target.score -= stolen
                current.score += stolen
                participationRepository.saveAll(listOf(target, current))
                logger.info("Player ${current.user.name} stole $stolen points from ${target.user.name}")
            }

            EffectType.DOUBLEDOWN -> {
                current.score = minOf(current.score * 2, WINNING_SCORE)
                participationRepository.save(current)
                logger.info("Player ${current.user.name} doubled score to ${current.score}")
            }

            null -> TODO()
        }
    }

    private fun updateTurnOrder(game: GameSession, participants: List<Participation>) {
        if (game.nextPlayerSkip) {
            game.currentPlayerIndex = (game.currentPlayerIndex + 2) % participants.size
            game.nextPlayerSkip = false
        } else {
            game.currentPlayerIndex = (game.currentPlayerIndex + 1) % participants.size
        }
    }

    private fun getDeckCards(deckJson: String): MutableList<Card> {
        return objectMapper.readValue(deckJson, object : TypeReference<MutableList<Card>>() {})
    }

    private fun saveTurnHistory(
        game: GameSession,
        user: User,
        card: Card,
        newScore: Int
    ) {
        turnRepository.save(
            Turn(
                gameSession = game,
                user = user,
                card = objectMapper.writeValueAsString(card),
                resultScore = newScore,
                description = "Player ${user.name} played ${card.name}"
            )
        )
    }

    private fun initializeDeck(game: GameSession) {
        val cards = mutableListOf<Card>().apply {
            // Points Cards (60%)
            repeat(10) {
                addAll(
                    listOf(
                        Card(CardType.POINTS, "Small Reward", 3),
                        Card(CardType.POINTS, "Medium Reward", 5),
                        Card(CardType.POINTS, "Large Reward", 7)
                    )
                )
            }

            // Action Cards (40%)
            repeat(5) {
                addAll(
                    listOf(
                        Card(CardType.ACTION, "Block", 1, EffectType.BLOCK),
                        Card(CardType.ACTION, "Steal", 2, EffectType.STEAL),
                        Card(CardType.ACTION, "Double Down", 2, EffectType.DOUBLEDOWN)
                    )
                )
            }
        }.shuffled()

        game.deck = objectMapper.writeValueAsString(cards)
    }

    private fun finishGame(game: GameSession) {
        game.status = GameSessionStatus.FINISHED
        game.finishedAt = LocalDateTime.now()
    }

    private fun validateGameState(game: GameSession, user: User) {
        require(game.status == GameSessionStatus.IN_PROGRESS) { "Game is not active" }
        val currentPlayer = participationRepository.findByGameSession(game)
            .sortedBy { it.id }[game.currentPlayerIndex]
        require(currentPlayer.user.id == user.id) { "Not your turn" }
    }
}