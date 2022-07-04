package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.Effects
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.rotateAroundAxisY
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

open class CubeRenderer(target: Target, future: CompletableFuture<Target.Container>, option: EffectOption) :
    AbstractEffectRenderer(target, future, option) {

    companion object {
        private val RIGHT = Vector(1, 0, 0).normalize()
        private val UP = Vector(0, 1, 0).normalize()
    }


    open val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))


    override fun sendTo() {
        if (target is Target.Location) {
            val pos1 = target.value
            getContainer {
                forEachLocation {
                    show(pos1, this, option.step) {
                        spawnParticle(pos1,it)
                    }
                }
            }
        }
    }

    open fun show(minLoc: Location, maxLoc: Location, step: Double, callback: (Location) -> Unit) {
        val minX: Double = minLoc.x.coerceAtMost(maxLoc.x)
        val minY: Double = minLoc.y.coerceAtMost(maxLoc.y)
        val minZ: Double = minLoc.z.coerceAtMost(maxLoc.z)
        val maxX: Double = minLoc.x.coerceAtLeast(maxLoc.x)
        val maxY: Double = minLoc.y.coerceAtLeast(maxLoc.y)
        val maxZ: Double = minLoc.z.coerceAtLeast(maxLoc.z)
        val minLoc = Location(minLoc.world, minX, minY, minZ)
        val width = maxX - minX
        val height = maxY - minY
        val depth = maxZ - minZ
        var newOrigin = minLoc
        var vector = RIGHT.clone()
        for (i in 1..4) {
            val length: Double = if (i % 2 == 0) {
                depth
            } else {
                width
            }
            var j = 0.0
            while (j < height) {
                callback(newOrigin.clone().add(UP.clone().multiply(j)))
                j += step
            }
            j = 0.0
            while (j < length) {
                val spawnLoc = newOrigin.clone().add(vector.clone().multiply(j))
                callback(spawnLoc)
                callback(spawnLoc.add(0.0, height, 0.0))
                j += step
            }
            newOrigin = newOrigin.clone().add(vector.clone().multiply(length))
            vector = vector.rotateAroundAxisY(90.0)
        }
    }
}