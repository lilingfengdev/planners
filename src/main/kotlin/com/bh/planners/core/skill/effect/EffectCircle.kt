package com.bh.planners.core.skill.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.CircleRenderer

object EffectCircle : Effect() {

    override val name: String
        get() = "circle"

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {
        CircleRenderer(target!!, option.createContainer(target, context), option).sendTo()
    }


}
