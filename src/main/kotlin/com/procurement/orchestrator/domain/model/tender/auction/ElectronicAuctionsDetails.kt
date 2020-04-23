package com.procurement.orchestrator.domain.model.tender.auction

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update

class ElectronicAuctionsDetails(
    values: List<ElectronicAuctionsDetail> = emptyList()
) : List<ElectronicAuctionsDetail> by values, IdentifiableObjects<ElectronicAuctionsDetail, ElectronicAuctionsDetails> {

    override fun updateBy(src: ElectronicAuctionsDetails) = ElectronicAuctionsDetails(update(dst = this, src = src))
}
