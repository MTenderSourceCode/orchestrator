package com.procurement.orchestrator.domain.model.lot

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Lots(values: List<Lot> = emptyList()) : List<Lot> by values,
                                              IdentifiableObjects<Lot, Lots>,
                                              Serializable {

    constructor(lot: Lot) : this(listOf(lot))

    override operator fun plus(other: Lots) = Lots(this as List<Lot> + other as List<Lot>)

    override operator fun plus(others: List<Lot>) = Lots(this as List<Lot> + others)

    override fun updateBy(src: Lots) = Lots(update(dst = this, src = src))
}
