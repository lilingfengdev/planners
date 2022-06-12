package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.effect.renderer.CircleRenderer
import com.bh.planners.core.kether.effect.renderer.EffectRenderer
import com.bh.planners.core.pojo.Session
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Circle
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectCircle : EffectLoader<EffectCircle.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "circle"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun handler(target: Target?, option: EffectOption, session: Session): EffectRenderer {
            return CircleRenderer(target!!,option.createContainer(target, session), option)
        }
    }
}
