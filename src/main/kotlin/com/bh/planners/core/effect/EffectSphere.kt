package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.Sphere
import com.bh.planners.core.kether.game.ActionEffect
import taboolib.common5.Coerce

/**
 * effect sphere "FLAME 0 0 0 -sample 50 -radius 1"
 */
object EffectSphere : com.bh.planners.core.effect.Effect() {
    override val name: String
        get() = "sphere"

    val EffectOption.sample: Int
        get() = Coerce.toInteger(demand.get("sample", "50"))

    val EffectOption.radius: Double
        get() = Coerce.toDouble(demand.get("radius", "1"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        val effectSpawner = EffectSpawner(option)
        option.createContainer(target, context).thenAccept {
            it.forEachLocation {
                val sphere = Sphere(this, option.sample, option.radius, effectSpawner)
                sphere.show()
            }
        }
    }
}