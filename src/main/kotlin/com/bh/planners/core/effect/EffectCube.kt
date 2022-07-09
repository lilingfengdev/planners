package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.CubeRenderer

object EffectCube : com.bh.planners.core.effect.Effect() {

    override val name: String
        get() = "cube"

    override fun sendTo(target: com.bh.planners.core.effect.Target?, option: com.bh.planners.core.effect.EffectOption, context: Context) {
        if (target !is com.bh.planners.core.effect.Target.Location) return
        CubeRenderer(target, option.createContainer(target, context), option).sendTo()
    }

}
