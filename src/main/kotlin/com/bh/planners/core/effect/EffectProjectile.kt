package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common5.Coerce

object EffectProjectile : Effect() {
    override val name: String
        get() = "projectile"

    val EffectOption.effect: String
        get() = this.demand.get("effect", "arc")!!

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.amount: Int
        get() = Coerce.toInteger(this.demand.get("amount", "5.0"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val step = option.step
        val amount = option.amount
        val period = option.period
        val effectSpawner = EffectSpawner(option)
        option.createContainer(target, context).thenAccept { container ->
            container.forEachLocation {
                val builder = Builder(this, step, amount, effectSpawner)
                ParticleFrame.create(period, builder,response)
            }
        }
    }

    class Builder(val location: Location, val step: Double, val amount: Int, spawner: EffectSpawner) :
        ParticleFrame.Builder(spawner) {

        val direction = location.direction

        var index = 0;

        override fun next(): Location? {
            while (index < amount) {
                index++
                direction.multiply(step)
                return location.add(direction)
            }
            return null
        }


    }

}