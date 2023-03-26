package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.ifEntity
import com.bh.planners.core.effect.Target.Companion.ifLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import com.bh.planners.util.entityAt
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import java.util.concurrent.CompletableFuture
import kotlin.math.floor

/**
 * 选中视角所看向的实体集群
 * step 最大距离
 * @visual 10
 * @vi 10
 */
object Visual : Selector {
    override val names: Array<String>
        get() = arrayOf("visual", "vi")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val range = data.read<Double>(0,"1")
        val future = CompletableFuture<Void>()
        submit {
            data.origin.ifLocation {
                data.container += getTargetLocation(this.value, this.value.direction, range).map { it.toTarget() }
            }
            data.origin.ifEntity {
                data.container += getTargetLocation(value, value.direction, range).map { it.toTarget() }
            }
            future.complete(null)
        }
        return future
    }


    /**
     * by SkillAPI
     */
    private fun getTargetLocation(start: Location, dir: Vector, maxRange: Double): Set<LivingEntity> {
        var maxRange = maxRange
        val list = mutableSetOf<LivingEntity>()
        if (dir.x == 0.0) {
            dir.x = java.lang.Double.MIN_NORMAL
        }
        if (dir.y == 0.0) {
            dir.y = java.lang.Double.MIN_NORMAL
        }
        if (dir.z == 0.0) {
            dir.y = java.lang.Double.MIN_NORMAL
        }
        val ox = if (dir.x > 0) 1 else 0
        val oy = if (dir.y > 0) 1 else 0
        val oz = if (dir.z > 0) 1 else 0
        while (maxRange > 0) {
            val dxt: Double = computeWeight(start.x, dir.x, ox)
            val dyt: Double = computeWeight(start.y, dir.y, oy)
            val dzt: Double = computeWeight(start.z, dir.z, oz)
            val t = dxt.coerceAtMost(dyt.coerceAtMost(dzt.coerceAtMost(maxRange))) + 1E-5
            start.add(dir.clone().multiply(t))
            maxRange -= t
            list += start.entityAt()
        }
        return list
    }

    private fun computeWeight(value: Double, dir: Double, offset: Int): Double {
        return (floor(value + offset) - value) / dir
    }


}