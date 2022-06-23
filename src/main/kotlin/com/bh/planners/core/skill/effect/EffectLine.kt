package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.skill.effect.renderer.LineRenderer
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction

object EffectLine : Effect() {

    override val name: String
        get() = "line"

    override fun sendTo(target: Target?, option: EffectOption, session: Session) {

        if (target !is Target.Location) return

        LineRenderer(target, option.createContainer(target, session), option, session)
    }

}
