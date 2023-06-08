package com.bh.planners.core.effect

import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context

object EffectCube : Effect() {

    override val name: String
        get() = "cube"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {
        if (target !is Target.Location) return
//        CubeRenderer(target, option.createContainer(target, context), option).sendTo()
    }

}
