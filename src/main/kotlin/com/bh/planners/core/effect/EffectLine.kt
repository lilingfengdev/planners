package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.LineRenderer

object EffectLine : com.bh.planners.core.effect.Effect() {

    override val name: String
        get() = "line"

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {

        if (target !is Target.Location) return

        LineRenderer(target, option.createContainer(target, context), option, context)
    }

}
