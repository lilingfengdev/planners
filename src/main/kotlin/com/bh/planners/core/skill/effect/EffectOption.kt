package com.bh.planners.core.skill.effect

import com.bh.planners.api.common.Demand
import com.bh.planners.api.common.Demand.Companion.toDemand
import com.google.common.base.Enums
import taboolib.common.platform.ProxyParticle
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import java.awt.Color

/**
 * by Chemdah
 */
open class EffectOption(text: String) {

    val demand = text.toDemand()
    val particle = Enums.getIfPresent(ProxyParticle::class.java, demand.namespace.uppercase()).or(ProxyParticle.FLAME)!!
    val offsetX = Coerce.toDouble(demand.get(1, "0")!!)
    val offsetY = Coerce.toDouble(demand.get(2, "0")!!)
    val offsetZ = Coerce.toDouble(demand.get(3, "0")!!)
    val posX = Coerce.toDouble(demand.get("posX"))
    val posY = Coerce.toDouble(demand.get("posY"))
    val posZ = Coerce.toDouble(demand.get("posZ"))
    val speed = Coerce.toDouble(demand.get(listOf("speed", "s"), "0")!!)
    val count = Coerce.toInteger(demand.get(listOf("count", "c"), "1")!!)

    val offsetVector = Vector(offsetX, offsetY, offsetZ)

    var data: ProxyParticle.Data? = null

    init {
        demand.get("block")?.let {
            data = ProxyParticle.BlockData(it)
        }
        demand.get("item")?.let {
            data = ProxyParticle.ItemData(it.split(":")[0], Coerce.toInteger(it.split(":").getOrElse(1) { "0" }))
        }
        demand.get("color")?.let {
            val color = it.split("~")[0].split(",")
            data = ProxyParticle.DustData(
                Color(
                    Coerce.toInteger(color.getOrElse(0) { "0" }),
                    Coerce.toInteger(color.getOrElse(1) { "1" }),
                    Coerce.toInteger(color.getOrElse(2) { "2" })
                ),
                Coerce.toFloat(it.split("~").getOrElse(1) { "0" })
            )
        }
    }

}
