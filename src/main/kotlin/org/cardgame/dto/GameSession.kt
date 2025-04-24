package org.cardgame.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "game_sessions")
data class GameSession(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Enumerated(EnumType.STRING)
    var status: GameSessionStatus = GameSessionStatus.WAIT_FOR_PLAYERS,

    @Column(columnDefinition = "TEXT")
    var deck: String = "[]",  // JSON представление списка карт

    @Column(name = "current_player_index")
    var currentPlayerIndex: Int = 0,

    @Column(name = "next_player_skip")
    var nextPlayerSkip: Boolean = false,

    @ManyToOne
    @JoinColumn(name = "created_by")
    val createdBy: User,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null,

    @Column(name = "finished_at")
    var finishedAt: LocalDateTime? = null
)