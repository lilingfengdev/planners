package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createPolygon

object EffectPolygon : CustomEffect {

    override val name: String
        get() = "polygon"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {

        val origin = parseTargetContainer(option.origin, context).firstLocation() ?: context.origin.getLocation()!!

        val radius = option.radius

        val sides = option.sides
        val step = option.step
        val period = option.period

        return createPolygon(adaptLocation(origin), radius, sides, step, period)
    }

}
