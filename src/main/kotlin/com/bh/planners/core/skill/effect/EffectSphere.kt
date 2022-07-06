package com.bh.planners.core.skill.effect

import com.bh.planners.core.kether.catchRunning
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.common.Sphere
import taboolib.common5.Coerce

/**
 * effect sphere "FLAME 0 0 0 -sample 50 -radius 1"
 */
object EffectSphere : Effect() {
    override val name: String
        get() = "sphere"

    val EffectOption.sample: Int
        get() = Coerce.toInteger(demand.get("sample", "50"))

    val EffectOption.radius: Double
        get() = Coerce.toDouble(demand.get("radius", "1"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {
        catchRunning {
            val location = target as? Target.Location ?: return@catchRunning
            val sphere = Sphere(location.value, option.sample, option.radius, EffectSpawner(option))
            sphere.show()
        }
    }
}