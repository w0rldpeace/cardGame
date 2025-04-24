package org.cardgame.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

enum class CardType { POINTS, ACTION }
enum class EffectType { BLOCK, STEAL, DOUBLEDOWN }

data class Card @JsonCreator constructor(
    @JsonProperty("type") var type: CardType,
    @JsonProperty("name") var name: String,
    @JsonProperty("value") var value: Int,
    @JsonProperty("effectType") var effectType: EffectType? = null
) {
    // Secondary constructor for Jackson
    constructor() : this(CardType.POINTS, "", 0)
}