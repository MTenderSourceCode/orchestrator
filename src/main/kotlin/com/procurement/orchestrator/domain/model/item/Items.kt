package com.procurement.orchestrator.domain.model.item

import com.procurement.orchestrator.domain.model.IdentifiableObjects
import com.procurement.orchestrator.domain.model.IdentifiableObjects.Companion.update
import java.io.Serializable

class Items(values: List<Item> = emptyList()) : List<Item> by values,
                                                IdentifiableObjects<Item, Items>,
                                                Serializable {

    constructor(value: Item) : this(listOf(value))

    override operator fun plus(other: Items) = Items(this as List<Item> + other as List<Item>)

    override operator fun plus(others: List<Item>) = Items(this as List<Item> + others)

    override fun updateBy(src: Items) = Items(update(dst = this, src = src))
}
