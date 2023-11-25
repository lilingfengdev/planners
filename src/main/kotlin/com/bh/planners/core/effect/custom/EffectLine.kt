package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createLine

object EffectLine : CustomEffect {

    override val name: String
        get() = "line"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {
        val start = parseTargetContainer(option.markA, context).firstLocation() ?: error("$name no MarkA")
        val end = parseTargetContainer(option.markB, context).firstLocation() ?: error("$name no MarkB")
        val step = option.step
        val period = option.period

        return createLine(adaptLocation(start), adaptLocation(end), step, period)
    }

}