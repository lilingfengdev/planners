package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createRay
import taboolib.module.effect.shape.Ray

object EffectRay : CustomEffect {

    override val name: String
        get() = "ray"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {
        val origin = parseTargetContainer(option.origin, context).firstLocation() ?: context.origin.getLocation()!!

        val length = option.length
        val step = option.step
        val range = option.range
        val period = option.period

        return createRay(adaptLocation(origin), adaptLocation(origin).direction, length, step, range, Ray.RayStopType.MAX_LENGTH, period)
    }

}