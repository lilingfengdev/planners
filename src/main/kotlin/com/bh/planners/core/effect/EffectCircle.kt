package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.CircleRenderer

object EffectCircle : com.bh.planners.core.effect.Effect() {

    override val name: String
        get() = "circle"

    override fun sendTo(target: com.bh.planners.core.effect.Target?, option: com.bh.planners.core.effect.EffectOption, context: Context) {
        CircleRenderer(target!!, option.createContainer(target, context), option).sendTo()
    }


}
