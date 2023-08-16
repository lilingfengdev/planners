package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import org.bukkit.entity.Entity
import java.util.concurrent.CompletableFuture

/**
 * 选中根据原点来定义的环状实体
 * @annular 2 3 5
 * @annular min max high
 *
 */
object Annular : Selector {

    override val names: Array<String>
        get() = arrayOf("annular")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        val min = data.read<Double>(0, "0.0")
        val max = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")

        return createAwaitVoidFuture {
            val livingEntitys = location.world?.livingEntities ?: return@createAwaitVoidFuture
            val entitys = mutableSetOf<Entity>()
            livingEntitys.forEach {
                if (it.location.direction.isInSphere(location.direction, max) && !it.location.direction.isInSphere(location.direction, min) && it.location.y <= location.y + high) {
                    entitys.add(it)
                }
            }
            entitys.forEach {
                data.container += it.toTarget()
            }
        }
    }

}
