package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.effect.getNearbyEntities
import com.bh.planners.core.effect.isPointInEntitySector
import com.bh.planners.core.selector.Selector
import org.bukkit.Location
import java.util.concurrent.CompletableFuture
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * radius(半径) angle(角度) ignoreOrigin(忽略远点,默认true) yaw(偏航角偏移)
 *
 */
object Sector : Selector {

    override val names: Array<String>
        get() = arrayOf("sector", "!sector")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        // 修正视角位置 相对 -0.5
        multiplyDirection(location,-0.5)

        val radius = data.read<Double>(0, "1")
        val angle = data.read<Double>(1, "0")
        val ignoreOrigin = data.read<Boolean>(2, "true")
        val yaw = data.read<Float>(3, "0.0")
        location.yaw += yaw

        return createAwaitVoidFuture {
            val nearbyEntities = location.getNearbyEntities(radius+10)
            nearbyEntities.forEach { entity ->
                if (isPointInEntitySector(entity.location, location, radius + sqrt( entity.width.pow( 2.0 ) * 2 ), angle)) {
                    if (data.isNon) {
                        data.container.removeIf { t -> t == entity }
                    }
                    // 如果忽略原点并且他不是原点 则添加
                    // 如果是原点 则跳过
                    else if (ignoreOrigin && (entity != data.origin)) {
                        data.container += entity.toTarget()
                    }
                }
            }
        }
    }

    private fun multiplyDirection(location: Location, step: Double) {
        val direction = location.direction
        direction.multiply(step)
        location.add(direction)
    }

}
