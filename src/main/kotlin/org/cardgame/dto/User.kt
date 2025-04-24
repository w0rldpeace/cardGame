package org.cardgame.dto

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true, nullable = false)
    val login: String,
    @Column(nullable = false)
    val passwordHash: String,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)