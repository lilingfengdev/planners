package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.createAwaitVoidFuture
import com.bh.planners.core.selector.Selector
import org.bukkit.util.Vector
import java.util.concurrent.CompletableFuture

/**
 * 视角前长方形
 * Long 长
 * wide 宽
 * high 高
 * forward 向前偏移
 *
 * @rectangle Long wide high forward
 */
object Rectangle : Selector {

    override val names: Array<String>
        get() = arrayOf("rectangle", "rec")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)

        val long = data.read<Double>(0, "0.0")
        val wide = data.read<Double>(1, "0.0")
        val high = data.read<Double>(2, "0.0")
        val forward = data.read<Double>(3, "0.0")

        val vectorX1 = location.direction.clone().setY(0).normalize().multiply(forward)
        val vectorY1 = Vector(0.0,-high/2,0.0)
        val vectorZ1 = location.direction.clone().setY(0).crossProduct(Vector(0,1,0)).multiply(wide/2)

        val vector1 = location.direction.clone().add(vectorX1).add(vectorY1).add(vectorZ1)

        val vectorX2 = location.direction.clone().setY(0).normalize().multiply(forward+long)
        val vectorY2 = Vector(0.0,high/2,0.0)
        val vectorZ2 = location.direction.clone().setY(0).crossProduct(Vector(0,1,0)).multiply(-wide/2)

        val vector2 = location.direction.clone().add(vectorX2).add(vectorY2).add(vectorZ2)

        return createAwaitVoidFuture {
            location.world?.livingEntities?.forEach {
                if (it.location.direction.isInAABB(vector1, vector2)) {
                    data.container += it.toTarget()
                }
            }
        }
    }

}