package com.procurement.orchestrator.domain.model.tender

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.orchestrator.domain.EnumElementProvider

enum class TenderStatusDetails(@JsonValue override val key: String) : EnumElementProvider.Key {

    AUCTION("auction"),
    AWARDED_CONTRACT_PREPARATION("awardedContractPreparation"),
    AWARDED_STANDSTILL("awardedStandStill"),
    AWARDED_SUSPENDED("awardedSuspended"),
    AWARDING("awarding"),
    CANCELLATION("cancellation"),
    CLARIFICATION("clarification"),
    COMPLETE("complete"),
    EMPTY("empty"),
    NEGOTIATION("negotiation"),
    PLANNED("planned"),
    PLANNING("planning"),
    SUSPENDED("suspended"),
    TENDERING("tendering");

    override fun toString(): String = key

    companion object : EnumElementProvider<TenderStatusDetails>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
