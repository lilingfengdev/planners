package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.ifEntity
import com.bh.planners.core.kether.effect.Target.Companion.ifLocation
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import kotlin.math.floor

/**
 * 视角所看向的方块
 * step 距离
 * -@<blockAt/ba> 10
 */
object BlockAt : Selector {

    val AIR_BLOCKS = Material.values().filter { it.isTransparent }

    override val names: Array<String>
        get() = arrayOf("blockAt", "ba")

    override fun check(name: String, target: Target?, args: String, session: Session, container: Target.Container) {
        val distance = if (args.isEmpty()) 10.0 else Coerce.toDouble(args)

        var block: Block? = null

        target?.ifEntity {
            block = getTargetLocation(livingEntity.eyeLocation, value.direction, distance).block
        }
        target?.ifLocation {
            block = getTargetLocation(value, value.direction, distance).block
        }

        if (block != null) {
            when (block!!.chunk.isLoaded) {
                true -> {
                    if (block!!.type !in AIR_BLOCKS) {
                        container.add(block!!.location.toTarget())
                    }
                }
                false -> {
                    submit(now = true, async = true) {
                        block!!.chunk.load(true)
                    }
                    if (block!!.type !in AIR_BLOCKS) {
                        container.add(block!!.location.toTarget())
                    }
                }
            }
        }


    }

    /**
     * by SkillAPI
     */
    private fun getTargetLocation(start: Location, dir: Vector, maxRange: Double): Location {
        var maxRange = maxRange
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
        while (start.block.type in AIR_BLOCKS && maxRange > 0) {
            val dxt: Double = computeWeight(start.x, dir.x, ox)
            val dyt: Double = computeWeight(start.y, dir.y, oy)
            val dzt: Double = computeWeight(start.z, dir.z, oz)
            val t = dxt.coerceAtMost(dyt.coerceAtMost(dzt.coerceAtMost(maxRange))) + 1E-5
            start.add(dir.clone().multiply(t))
            maxRange -= t
        }
        return start
    }


    private fun computeWeight(value: Double, dir: Double, offset: Int): Double {
        return (floor(value + offset) - value) / dir
    }

}