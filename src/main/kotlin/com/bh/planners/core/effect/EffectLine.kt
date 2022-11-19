package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.common.Line
import com.bh.planners.core.effect.renderer.LineRenderer
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Session
import com.bh.planners.util.entityAt
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common5.Coerce

object EffectLine : Effect() {

    override val name: String
        get() = "line"


    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))


    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

        if (target !is Target.Location) return


        // 忽略捕获释放者
        response.addListener("@Line:Ignore") {

            val entity = target.getEntity()
            removeIf { it == entity }

        }

        val step = option.step
        val period = option.period
        val spawner = EffectSpawner(option)
        option.createContainer(target, context).thenAccept { container ->

            container.forEachLocation {

                val builder = Builder(target.value, this, step, spawner)
                ParticleFrame.create(period, builder, response)

            }

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
            if (i < vectorLength) {
                val location = locA.clone().add(vectorAB.clone().multiply(i))
                i += step
                return location
            }
            return null
        }
    }

}
