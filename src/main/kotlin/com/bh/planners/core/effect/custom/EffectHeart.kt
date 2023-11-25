package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createHeart

object EffectHeart : CustomEffect {

    override val name: String
        get() = "heart"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {

        val scaleX = option.scaleX
        val scaleY = option.scaleY

        val origin = parseTargetContainer(option.origin, context).firstLocation() ?: context.origin.getLocation()!!

        val period = option.period

        return createHeart(scaleX, scaleY, adaptLocation(origin), period)
    }

}
