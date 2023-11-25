package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createFilledCircle

object EffectFilledCircle : CustomEffect {
    override val name: String
        get() = "filledcircle"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {

        val origin = parseTargetContainer(option.origin, context).firstLocation() ?: context.origin.getLocation()!!

        val radius = option.radius

        val sample = option.sample
        val period = option.period

        return createFilledCircle(adaptLocation(origin), radius, sample, period)

    }

}