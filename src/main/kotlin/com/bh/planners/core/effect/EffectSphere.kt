package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.api.common.ParticleFrame.Companion.new
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.forEachLocation
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common5.mirrorNow
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
        option.createContainer(context).forEachLocation {
            ParticleFrame.create(ParticleFrame.FrameBuilder().new {
                time(option.period)
                response(response)
                builder(Builder(this@forEachLocation, option.sample, option.radius, effectSpawner))
            })
        }
    }

    class Builder(val location: Location, val sample: Int, val radius: Double, spawner: EffectSpawner) :
        ParticleFrame.Builder(spawner) {

        /**
         * 黄金角度 约等于137.5度
         */
        private val phi = Math.PI * (3.0 - sqrt(5.0))

        var index = 0

        override fun next(): Location? {
            return mirrorNow("渲染粒子Sphere") {
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
                    location.clone().add(x, y, z)
                } else {
                    null
                }
            }
        }

        override fun nexts(): List<Location> {
            return mirrorNow("渲染粒子Sphere") {
                val locations = mutableListOf<Location>()
                while (index < sample) {
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
                    locations.add(location.clone().add(x, y, z))
                }
                locations
            }
        }

    }

}