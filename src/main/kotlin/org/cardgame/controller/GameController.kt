package org.cardgame.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.cardgame.dto.CustomUserDetails
import org.cardgame.dto.GameStateResponse
import org.cardgame.repository.ParticipationRepository
import org.cardgame.service.GameService
import org.cardgame.util.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/games")
class GameController(
    private val gameService: GameService,
    private val participationRepository: ParticipationRepository,
    private val objectMapper: ObjectMapper
) {
    @PostMapping
    fun createGame(@AuthenticationPrincipal user: CustomUserDetails): ResponseEntity<GameStateResponse> {
        val game = gameService.createGame(user.getUserEntity())
        return ResponseEntity.ok(game.toResponse(emptyList(), objectMapper))
    }

    @PostMapping("/{gameId}/join")
    fun joinGame(
        @PathVariable gameId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<GameStateResponse> {
        gameService.joinGame(gameId, user.getUserEntity())
        val game = gameService.getGame(gameId)
        val participations = participationRepository.findByGameSession(game)
        return ResponseEntity.ok(game.toResponse(participations, objectMapper))
    }

    @PostMapping("/{gameId}/start")
    fun startGame(
        @PathVariable gameId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<GameStateResponse> {
        gameService.startGame(gameId, user.getUserEntity())
        val game = gameService.getGame(gameId)
        val participations = participationRepository.findByGameSession(game)
        return ResponseEntity.ok(game.toResponse(participations, objectMapper))
    }

    @GetMapping("/{gameId}")
    fun getGameStatus(
        @PathVariable gameId: Long
    ): ResponseEntity<GameStateResponse> {
        val game = gameService.getGame(gameId)
        val participations = participationRepository.findByGameSession(game)
        return ResponseEntity.ok(game.toResponse(participations, objectMapper))
    }

    @PostMapping("/{gameId}/turn")
    fun makeTurn(
        @PathVariable gameId: Long,
        @AuthenticationPrincipal user: CustomUserDetails
    ): ResponseEntity<GameStateResponse> {
        val game = gameService.processTurn(gameId, user.getUserEntity())
        val participations = participationRepository.findByGameSession(game)
        return ResponseEntity.ok(game.toResponse(participations, objectMapper))
    }
}