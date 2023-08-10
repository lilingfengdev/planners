package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
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

        val future = CompletableFuture<Void>()
        return createAwaitVoidFuture {
            val maxEntitys = location.world?.getNearbyEntities(location, max, high, max)
            val minEntitys = location.world?.getNearbyEntities(location, min, 256.0, min)
            val entitys = mutableSetOf<Entity>()
            maxEntitys?.let { entitys.addAll(it) }
            minEntitys?.let { entitys.removeAll(minEntitys) }
            entitys.forEach {
                if (it is LivingEntity) {
                    data.container += it.toTarget()
                }
            }
        }
    }

}
