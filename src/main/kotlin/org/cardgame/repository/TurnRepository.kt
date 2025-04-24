package org.cardgame.repository

import org.cardgame.dto.Turn
import org.springframework.data.jpa.repository.JpaRepository

interface TurnRepository : JpaRepository<Turn, Long>