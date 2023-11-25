package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createCube

object EffectCube : CustomEffect {

    override val name: String
        get() = "cube"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {
        val min = parseTargetContainer(option.markA, context).firstLocation() ?: error("$name no MarkA")
        val max = parseTargetContainer(option.markB, context).firstLocation() ?: error("$name no MarkB")
        val step = option.step
        val period = option.period

        return createCube(adaptLocation(min), adaptLocation(max), step, period)
    }

}
