package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.CircleRenderer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction

object EffectCircle : Effect() {

    override val name: String
        get() = "circle"

    override fun sendTo(target: Target?, option: EffectOption, session: Session) {
        CircleRenderer(target!!, option.createContainer(target, session), option).sendTo()
    }


}
