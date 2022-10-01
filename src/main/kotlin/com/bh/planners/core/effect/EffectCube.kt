package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.renderer.CubeRenderer
import com.bh.planners.core.kether.game.ActionEffect

object EffectCube : Effect() {

    override val name: String
        get() = "cube"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        if (target !is Target.Location) return
        CubeRenderer(target, option.createContainer(target, context), option).sendTo()
    }

}
