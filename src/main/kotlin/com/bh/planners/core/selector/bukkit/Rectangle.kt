package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import org.bukkit.entity.LivingEntity
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

/**
 * 视角前长方形
 * Long 长
 * wide 宽
 * high 高
 *
 * @rectangle Long wide high
 */
object Rectangle : Selector {

    override val names: Array<String>
        get() = arrayOf("rectangle", "rec")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        val long = data.read<Double>(0, "0.0")
        val wide = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")

        val yaw1 = data.origin.getLocation()?.yaw?.toDouble() ?: 0.0
        val yaw: Double = if (yaw1 > 180.0) {
            Math.toRadians(yaw1 - 360.0)
        } else if (yaw1 < -180.0) {
            Math.toRadians(yaw1 + 360.0)
        } else {
            Math.toRadians(yaw1)
        }

        var n = wide/2.0
        var m = wide/2.0

        return createAwaitVoidFuture {
            while (m < (long - (wide / 2.0))) {
                location.x -= n * sin(yaw)
                location.z += n * cos(yaw)
                location.world?.getNearbyEntities(location, wide / 2.0, high, wide / 2.0)?.forEach {
                    if (it is LivingEntity) {
                        data.container += it.toTarget()
                    }
                }
                n = 1.0.coerceAtMost(wide / 2.0)
                m += 1.0.coerceAtMost(wide / 2.0)
            }
        }
    }
}