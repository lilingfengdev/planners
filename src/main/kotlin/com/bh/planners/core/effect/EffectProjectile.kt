package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common5.mirrorNow

object EffectProjectile : Effect() {
    override val name: String
        get() = "projectile"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

        val entity = target?.getEntity() ?: return
        // 忽略捕获释放者
        response.eventTicks.forEach {
            if (it is EffectICallback.Tick) {
                it.listeners["@Ignore"] = { it.removeIf { entity == it } }
            }
        }

        val step = option.step
        val amount = option.amount
        val period = option.period
        val effectSpawner = EffectSpawner(option)
        option.createContainer(context).thenAccept { container ->
            container.forEachLocation {
                val builder = Builder(this, step, amount, effectSpawner)
                ParticleFrame.create(period, builder, response)
            }
        }
    }

    class Builder(val location: Location, val step: Double, val amount: Int, spawner: EffectSpawner) :
        ParticleFrame.Builder(spawner) {

        val direction = location.direction

        var index = 0

        override fun next(): Location? {
            return mirrorNow("渲染粒子Projectile") {
                if (index < amount) {
                    index++
                    direction.multiply(step)
                    location.add(direction)
                } else {
                    null
                }
            }
        }

        override fun nexts(): List<Location> {
            return mirrorNow("渲染粒子Projectile") {
                val locations = mutableListOf<Location>()
                while (index < amount) {
                    index++
                    direction.multiply(step)
                    locations.add(location.add(direction))
                }
                locations
            }
        }


    }

}