package com.bh.planners.core.skill.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.CubeRenderer

object EffectCube : Effect() {

    override val name: String
        get() = "cube"

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {
        if (target !is Target.Location) return
        CubeRenderer(target, option.createContainer(target, context), option).sendTo()
    }

}
