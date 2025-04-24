package org.cardgame.dto

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "participations")
data class Participation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    val gameSession: GameSession,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    var score: Int = 0,
    var isActive: Boolean = true
)