package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.ArcRenderer

object EffectArc : Effect() {

    override val name: String
        get() = "arc"

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {
        ArcRenderer(target!!, option.createContainer(target, context), option).sendTo()
    }

}
