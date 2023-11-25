package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createLotus

object EffectLotus : CustomEffect {

    override val name: String
        get() = "lotus"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {
        val origin = parseTargetContainer(option.origin, context).firstLocation() ?: context.origin.getLocation()!!
        val period = option.period

        return createLotus(adaptLocation(origin), period)
    }

}