package org.cardgame.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "turns")
data class Turn(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "game_session_id")
    val gameSession: GameSession,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(columnDefinition = "TEXT")
    val card: String,  // JSON представление карты

    @Column(name = "result_score")
    val resultScore: Int,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "TEXT")
    val description: String = ""
)