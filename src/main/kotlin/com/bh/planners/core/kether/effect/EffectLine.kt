package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.effect.renderer.EffectRenderer
import com.bh.planners.core.kether.effect.renderer.LineRenderer
import com.bh.planners.core.pojo.Session
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Line
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectLine : EffectLoader<EffectLine.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "line"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer {

            if (target !is Target.Location) return EFFECT_AIR

            return LineRenderer(target, option.createContainer(target, session), option)
        }
    }
}
