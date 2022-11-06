package com.bh.planners.core.effect

import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin

object EffectRelativeLine : Effect() {
    override val name: String
        get() = "rel-line"

    val EffectOption.effect: String
        get() = this.demand.get("effect", "arc")!!

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.amount: Int
        get() = Coerce.toInteger(this.demand.get("amount", "5.0"))

    fun createDot(location: Location, radius: Double): Location {
        val coordinate = PlayerFrontCoordinate(location)
        coordinate.originDot.pitch = location.pitch
        return coordinate.newLocation(radius, 0.0, 0.0)
    }

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val step = option.step
        val amount = option.amount
        val effectSpawner = EffectSpawner(option)
        option.createContainer(target, context).thenAccept { container ->
            container.forEachLivingEntity {
                val locations = mutableListOf<Location>()
                repeat(amount) { index ->
                    locations += createDot(this.eyeLocation, step * index)
                }
                locations.forEach {
                    println(it)
                    effectSpawner.spawn(it)
                }
            }
        }
    }
}