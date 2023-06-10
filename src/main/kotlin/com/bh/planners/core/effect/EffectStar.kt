package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos

object EffectStar : Effect() {
    override val name: String
        get() = "star"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

    }

    class BuilderSingle(val origin: Target, val radius: Double, val step: Double, spawner: EffectSpawner) :
        ParticleFrame.Builder(spawner) {

        val vector = Vector(1, 0, 0)

        // 转弧度制
        var radians = Math.toRadians((72 * 2).toDouble())
        var x = radius * Math.cos(radians)
        var y = 0.0
        var z = radius * Math.sin(radians)
        var end: Location = origin.getLocation()!!.clone().add(x, y, z)
        var length: Double = cos(Math.toRadians(36.0)) * radius * 2

        var index = 0.0

        override fun next(): Location? {

            for (i in 1..5) {
                if (index < length) {
                    val vectorTemp: Vector = vector.clone().multiply(index)
                    val spawnLocation = end.clone().add(vectorTemp)
                    index += step
                    return spawnLocation
                }
                val vectorTemp: Vector = vector.clone().multiply(length)
                end = end.clone().add(vectorTemp)
                rotateAroundAxisY(vector, -144.0)
            }
            return null
        }

        override fun nexts(): List<Location> {
            val locations = mutableListOf<Location>()
            for (i in 1..5) {
                while (index < length) {
                    val vectorTemp: Vector = vector.clone().multiply(index)
                    val spawnLocation = end.clone().add(vectorTemp)
                    index += step
                    locations.add(spawnLocation)
                }
                val vectorTemp: Vector = vector.clone().multiply(length)
                end = end.clone().add(vectorTemp)
                rotateAroundAxisY(vector, -144.0)
            }
            return locations
        }

    }

}