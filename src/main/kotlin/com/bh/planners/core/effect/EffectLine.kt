package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.api.common.ParticleFrame.Companion.new
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.forEachLocation
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common5.mirrorNow

object EffectLine : Effect() {

    override val name: String
        get() = "line"


    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

        if (target !is Target.Location) return

        val spawner = EffectSpawner(option)
        option.createContainer(context).forEachLocation {
            ParticleFrame.create(ParticleFrame.FrameBuilder().new {
                builder(Builder(target.value, this@forEachLocation, option.step, spawner))
                time(option.period)
                response(response)
            })
        }

    }

    class Builder(val locA: Location, val locB: Location, val step: Double, spawner: EffectSpawner) :
        ParticleFrame.Builder(spawner) {

        val vectorAB = locB.clone().subtract(locA).toVector()
        val list = mutableListOf<Location>()
        val vectorLength = vectorAB.length()

        var i = 0.0

        init {
            vectorAB.normalize()
        }

        override fun next(): Location? {
            return mirrorNow("渲染粒子Line") {
                if (i < vectorLength) {
                    val location = locA.clone().add(vectorAB.clone().multiply(i))
                    i += step
                    location
                } else {
                    null
                }
            }
        }

        override fun nexts(): List<Location> {
            return mirrorNow("渲染粒子Line") {
                val locations = mutableListOf<Location>()
                while (i < vectorLength) {
                    val location = locA.clone().add(vectorAB.clone().multiply(i))
                    i += step
                    locations.add(location)
                }
                locations
            }
        }

    }

}
