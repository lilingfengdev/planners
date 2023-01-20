package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target.Companion.getLivingEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.isInsideSector
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * radius 半径
 * angle 角度
 */
object Sector : Selector {
    override val names: Array<String>
        get() = arrayOf("sector","!sector")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.target?.getLocation() ?: return CompletableFuture.completedFuture(null)
        val radius = data.read<Double>(0,"1")
        val angle = data.read<Double>(1,"0")
        val future = CompletableFuture<Void>()
        submit(async = false) {
            location.world?.getNearbyEntities(location, radius,radius,radius)?.filterIsInstance<LivingEntity>()?.forEach {
                if (isInsideSector(it.location,location,radius, angle)) {
                    if (data.isNon) {
                        data.container.removeIf { t -> t.getLivingEntity() == it }
                    } else {
                        data.container += it.toTarget()
                    }
                }
            }
            future.complete(null)
        }


        return future
    }
}