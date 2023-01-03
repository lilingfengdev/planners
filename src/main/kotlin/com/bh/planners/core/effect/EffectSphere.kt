package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.Sphere
import com.bh.planners.core.kether.game.ActionEffect
import org.bukkit.Location
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * effect sphere "FLAME 0 0 0 -sample 50 -radius 1"
 */
object EffectSphere : Effect() {
    override val name: String
        get() = "sphere"


    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val effectSpawner = EffectSpawner(option)
        val sample = option.sample
        val radius = option.radius
        val period = option.period
        option.createContainer(target, context).thenAccept {
            it.forEachLocation {
                val builder = Builder(this, sample, radius, effectSpawner)
                ParticleFrame.create(period, builder, response)
            }
        }
    }

    class Builder(val location: Location, val sample: Int, val radius: Double, spawner: EffectSpawner) : ParticleFrame.Builder(spawner) {

        /**
         * 黄金角度 约等于137.5度
         */
        private val phi = Math.PI * (3.0 - sqrt(5.0))

        var index = 0

        override fun next(): Location? {
            if (index < sample) {
                // y goes from 1 to -1
                var y = (1 - index / (sample - 1f) * 2).toDouble()
                // radius at y
                val yRadius = sqrt(1 - y * y)
                // golden angle increment
                val theta = phi * index
                val x = cos(theta) * radius * yRadius
                val z = sin(theta) * radius * yRadius
                y *= radius
                index++
                return location.clone().add(x, y, z)
            }
            return null
        }

    }

}