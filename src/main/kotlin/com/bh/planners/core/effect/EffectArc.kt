package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.ArcRenderer

object EffectArc : com.bh.planners.core.effect.Effect() {

    override val name: String
        get() = "arc"

    override fun sendTo(target: com.bh.planners.core.effect.Target?, option: com.bh.planners.core.effect.EffectOption, context: Context) {
        ArcRenderer(target!!, option.createContainer(target, context), option).sendTo()
    }

}
