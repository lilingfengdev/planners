package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.ifEntity
import com.bh.planners.core.kether.effect.Target.Companion.ifLocation
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import kotlin.math.floor

/**
 * 选中视角所看向的一条线
 * step 距离
 * -@aline 10
 * -@al 10
 */
object AngleLine : Selector {
    override val names: Array<String>
        get() = arrayOf("aline", "al")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val range = Coerce.toDouble(args)
        target?.ifLocation {
            container.addAll(getTargetLocation(this.value, this.value.direction, range).map { it.toTarget() })
        }
        target?.ifEntity {
            container.addAll(getTargetLocation(livingEntity.eyeLocation, value.direction, range).map { it.toTarget() })
        }
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

    fun Location.entityAt(): List<LivingEntity> {
        return world!!.getNearbyEntities(this, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>()
    }

}