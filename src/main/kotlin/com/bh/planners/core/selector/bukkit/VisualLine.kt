package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture
import kotlin.math.ceil

/**
 * length 距离
 * radius 半径
 */
object VisualLine : Selector {

    override val names: Array<String>
        get() = arrayOf("v-line","!v-line")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)
        val direction = location.direction
        val length = data.read<Double>(0,"5")
        val radius = data.read<Double>(1,"1")
        val amount = ceil(length / radius)
        val future = CompletableFuture<Void>()
        submit {
            repeat(amount.toInt()) {
                location.world?.getNearbyEntities(location.add(direction.multiply(radius)),radius,radius,radius)?.forEach {
                    if (data.isNon) {
                        data.container.removeIf { it.getEntity() == it }
                    } else {
                        data.container += it.location.toTarget()
                    }
                }
            }
            future.complete(null)
        }
        return future
    }
}