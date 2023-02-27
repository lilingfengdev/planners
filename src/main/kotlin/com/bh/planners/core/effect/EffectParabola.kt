package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import org.bukkit.Particle
import taboolib.common5.Coerce

object EffectParabola : Effect() {

    override val name: String
        get() = "parabola"


    val EffectOption.height: Double
        get() = Coerce.toDouble(demand.get(listOf("height","h"), "2.0"))

    val EffectOption.power: Double
        get() = Coerce.toDouble(demand.get(listOf("power","p"), "2.0"))

    val EffectOption.step: Double
        get() = Coerce.toDouble(demand.get(listOf("step","s"), "0.05"))

    val EffectOption.threshold: Double
        get() = Coerce.toDouble(demand.get(listOf("threshold","t"), "1"))


    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val step = option.step

        val height = option.height
        val power = option.power
        val period = option.period
        val threshold = option.threshold
        val spawner = EffectSpawner(option)
        option.createContainer(context).thenAccept { container ->
            container.forEachLocation {
                ParticleFrame.create(period,Builder(this,height,power,step,threshold,spawner),response)
            }
        }
    }

    fun getMidpoint(p0: Location,p1: Location) : Location {
        val x = (p0.x + p1.x) / 2
        val z = (p0.z + p1.z) / 2
        return Location(p0.world,x, p0.y, z)
    }

    fun Location.resetPitch(): Location {
        this.pitch = 0f
        return this
    }

    class Builder(origin: Location, height: Double, power: Double,val step : Double,val threshold: Double, spawner: EffectSpawner) : ParticleFrame.Builder(spawner) {

        val direction = origin.resetPitch().direction

        val p0 = origin.clone()
        val p2 = p0.clone().add(direction.multiply(power))
        val p1 = getMidpoint(p0,p2).add(0.0,height,0.0)

        var t = 0.0

        override fun next(): Location? {
            if (t < threshold) {
                val v1 = p1.clone().subtract(p0).toVector()
                val t1 = p0.clone().add(v1.multiply(t))
                val v2 = p2.clone().subtract(p1).toVector()
                val t2 = p1.clone().add(v2.multiply(t))
                val v3 = t2.clone().subtract(t1).toVector()
                val destination = t1.clone().add(v3.multiply(t))
                t += step
                return destination.clone()
            }
            return null
        }

    }


}