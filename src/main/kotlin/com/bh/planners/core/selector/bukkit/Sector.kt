package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.isPointInEntitySector
import com.bh.planners.core.selector.Selector
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture

/**
 * radius(半径) angle(角度) ignoreOrigin(忽略远点,默认true) yaw(偏航角偏移)
 *
 */
object Sector : Selector {
    override val names: Array<String>
        get() = arrayOf("sector", "!sector")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)
        val radius = data.read<Double>(0, "1")
        val angle = data.read<Double>(1, "0")
        val ignoreOrigin = data.read<Boolean>(2, "true")
        val yaw = data.read<Float>(3, "0.0")
        location.yaw += yaw
        val future = CompletableFuture<Void>()
        submit(async = false) {
            location.world?.getNearbyEntities(location, radius, radius, radius)?.filterIsInstance<LivingEntity>()
                ?.forEach {
                    if (isPointInEntitySector(it.location, location, radius, angle)) {
                        if (data.isNon) {
                            data.container.removeIf { t -> t == it }
                        }
                        // 如果忽略原点并且他不是原点 则添加
                        // 如果是原点 则跳过
                        else if (ignoreOrigin && it != data.origin) {
                            data.container += it.toTarget()
                        }
                    }
                }
            future.complete(null)
        }


        return future
    }
}