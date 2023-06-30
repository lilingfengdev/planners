package com.bh.planners.core.effect

import com.bh.planners.api.common.Demand.Companion.toDemand
import com.google.common.base.Enums
import taboolib.common.platform.ProxyParticle
import taboolib.common.util.Vector
import taboolib.common5.Coerce
import java.awt.Color
import java.util.*

/**
 * by Chemdah
 */
open class EffectOption(text: String) {


    companion object {

        private val cache = Collections.synchronizedMap(mutableMapOf<String, EffectOption>())

        fun get(text: String): EffectOption {
            return cache.computeIfAbsent(text) { EffectOption(text) }
        }

    }

    val demand = text.toDemand()

    val optOffsetVector = demand.get(listOf("offset", "os"), "0 0 0")!!.split(" ").map { Coerce.toDouble(it) }

    val particle =
        Enums.getIfPresent(ProxyParticle::class.java, demand.namespace.uppercase(Locale.getDefault())).or(ProxyParticle.FLAME)!!
    val offsetX = Coerce.toDouble(optOffsetVector.getOrElse(0) { "0" })
    val offsetY = Coerce.toDouble(optOffsetVector.getOrElse(1) { "0" })
    val offsetZ = Coerce.toDouble(optOffsetVector.getOrElse(2) { "0" })
    val posX = Coerce.toDouble(demand.get("posX"))
    val posY = Coerce.toDouble(demand.get("posY"))
    val posZ = Coerce.toDouble(demand.get("posZ"))
    val speed = Coerce.toDouble(demand.get(listOf("speed", "s"), "0")!!)
    val count = Coerce.toInteger(demand.get(listOf("count", "c"), "1")!!)

    val offsetVector = Vector(offsetX, offsetY, offsetZ)

    var data: ProxyParticle.Data? = null

    init {
        demand.get("block")?.let {
            val args = it.split(",")
            data = ProxyParticle.BlockData(
                args.getOrElse(0) { "DIRT" }.uppercase(Locale.getDefault()),
                Coerce.toInteger(args.getOrElse(1) { 0 })
            )
        }
        demand.get("item")?.let {
            data = ProxyParticle.ItemData(it.split(":")[0], Coerce.toInteger(it.split(":").getOrElse(1) { "0" }))
        }
        demand.get("color")?.let {
            val color = it.split("~")[0].split(" ")
            data = ProxyParticle.DustData(
                Color(
                    Coerce.toInteger(color.getOrElse(0) { "0" }),
                    Coerce.toInteger(color.getOrElse(1) { "1" }),
                    Coerce.toInteger(color.getOrElse(2) { "2" })
                ),
                Coerce.toFloat(it.split("~").getOrElse(1) { "1" })
            )
        }
    }


}
