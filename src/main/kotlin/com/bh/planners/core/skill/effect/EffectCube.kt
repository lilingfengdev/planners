package com.bh.planners.core.skill.effect

import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.renderer.CubeRenderer
import com.bh.planners.core.skill.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import taboolib.library.kether.ParsedAction

object EffectCube : EffectLoader<EffectCube.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "cube"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer {
            if (target !is Target.Location) return EFFECT_AIR
            return CubeRenderer(target, option.createContainer(target, session), option)
        }
    }

}
