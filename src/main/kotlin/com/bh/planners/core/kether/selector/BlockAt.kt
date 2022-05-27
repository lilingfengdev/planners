package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import kotlin.math.floor

object BlockAt : Selector {

    val AIR_BLOCKS = Material.values().filter { it.isTransparent }

    override val names: Array<String>
        get() = arrayOf("blockAt", "ba")

    override fun check(args: String, session: Session, sender: Player, container: Target.Container) {
        val distance = if (args.isEmpty()) 10.0 else Coerce.toDouble(args)
        val block = getTargetLocation(sender, distance).block
        when (block.chunk.isLoaded){
            true -> {
                if (block.type !in AIR_BLOCKS) {
                    container.add(block.location.toTarget())
                }
            }
            false -> {
                submit(now = true , async = true) {
                    block.chunk.load(true)
                }
                if (block.type !in AIR_BLOCKS) {
                    container.add(block.location.toTarget())
                }
            }
        }

    }
    /**
     * by SkillAPI
     */
    private fun getTargetLocation(entity: LivingEntity, maxRange: Double): Location {
        var maxRange = maxRange
        val start = entity.eyeLocation
        val dir = entity.location.direction
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