package com.procurement.orchestrator.application.model

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.model.UUID_PATTERN
import com.procurement.orchestrator.domain.model.isUUID
import java.io.Serializable
import java.util.*

class ResponseId private constructor(private val value: String) : Serializable {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ResponseId
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        val pattern: String
            get() = UUID_PATTERN

        fun validation(text: String): Boolean = text.isUUID()

        fun tryCreateOrNull(text: String): ResponseId? = if (validation(text)) ResponseId(text) else null

        fun generate(): ResponseId = ResponseId(UUID.randomUUID().toString())
    }
}
