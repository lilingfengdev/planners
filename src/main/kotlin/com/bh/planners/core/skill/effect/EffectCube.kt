package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.CubeRenderer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction

object EffectCube : Effect() {

    override val name: String
        get() = "cube"

    override fun sendTo(target: Target?, option: EffectOption, session: Session) {
        if (target !is Target.Location) return
        CubeRenderer(target, option.createContainer(target, session), option).sendTo()
    }

}
