package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.ArcRenderer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction

object EffectArc : EffectLoader<EffectArc.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "arc"


    class Impl(action: ParsedAction<*>) : Effect(action) {
        override fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer {
            return ArcRenderer(target!!, option.createContainer(target, session), option)
        }
    }

}
