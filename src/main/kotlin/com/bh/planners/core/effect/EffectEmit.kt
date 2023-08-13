package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.forEachLocation
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import org.bukkit.util.Vector
import taboolib.common5.mirrorNow

object EffectEmit : Effect() {


    //随机发散粒子
    override val name: String
        get() = "emit"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val spawner = EffectSpawner(option)

        val radius = option.radius
        val amount = option.amount

        option.createContainer(context).forEachLocation {
            val builder =
                Builder(this, radius, amount, spawner)
            ParticleFrame.create(option.period, builder, response)
        }

    }

    class Builder(
        val location: Location,
        val radius: Double,
        val amount: Int,
        spawner: EffectSpawner,
    ) : ParticleFrame.Builder(spawner) {

        var i = 0.0

        override fun next(): Location? {
            return mirrorNow("渲染粒子Emit") {
                if (i < amount) {
                    i++
                    val loc = location.direction.clone()
                    val vector = Vector.getRandom()
                    loc.add(vector.multiply(radius)).toLocation(location.world!!)
                } else {
                    null
                }
            }
        }

        override fun nexts(): List<Location> {
            return mirrorNow("渲染粒子Emit") {
                val  locations = mutableListOf<Location>()
                while (i < amount) {
                    i++
                    val loc = location.direction.clone()
                    val vector = Vector.getRandom()
                    loc.add(vector.multiply(radius)).toLocation(location.world!!)
                }
                locations
            }
        }

    }

}