package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.ifEntity
import com.bh.planners.core.skill.effect.Target.Companion.ifLocation
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture
import kotlin.math.floor

/**
 * 视角所看向的方块
 * -@blockAt [距离],[x偏移],[y偏移],[z偏移]
 * -@blockAt 10,0,0,0
 * -@blockAt 13.5,0.3,3.6,0.5
 */
object BlockAt : Selector {

    val AIR_BLOCKS = Material.values().filter { it.isTransparent }

    override val names: Array<String>
        get() = arrayOf("blockAt", "ba")

    override fun check(
        name: String,
        target: Target?,
        args: String,
        context: Context,
        container: Target.Container
    ): CompletableFuture<Void> {
        val arg = args.split(",")
        val distance = if (arg.isEmpty()) 10.0 else Coerce.toDouble(arg[0])
        val offsetX = if (arg.size <= 1) 0.0 else Coerce.toDouble(arg[1])
        val offsetY = if (arg.size <= 2) 0.0 else Coerce.toDouble(arg[2])
        val offsetZ = if (arg.size <= 3) 0.0 else Coerce.toDouble(arg[3])

        var block: Block? = null


        target?.ifEntity {
            block = getTargetLocation(this.value, value.direction, distance).block
        }
        target?.ifLocation {
            block = getTargetLocation(value, value.direction, distance).block
        }

        if (block != null) {
            var loc: Location? = null
            when (block!!.chunk.isLoaded) {
                true -> {
                    if (block!!.type !in AIR_BLOCKS) {
                        loc = block!!.location
                    }
                }

                false -> {
                    submit(now = true, async = true) {
                        block!!.chunk.load(true)
                    }
                    if (block!!.type !in AIR_BLOCKS) {
                        loc = block!!.location
                    }
                }
            }
            try {
                container.add(loc!!.add(offsetX, offsetY, offsetZ).toTarget())
            } catch (_: Exception) {}
        }
        return CompletableFuture.completedFuture(null)
    }

    /**
     * by SkillAPI
     */
    private fun getTargetLocation(start: Location, dir: Vector, maxRange: Double): Location {
        var maxRange = maxRange
        if (dir.x == 0.0) {
            dir.x = java.lang.Double.MIN_NORMAL
        }
        // 同步没事吧？
        // 啥意思 把你的代码顶掉
        // ok
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