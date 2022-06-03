package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.pojo.Session
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Arc
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectArc : EffectLoader<EffectArc.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "arc"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun sendTo(target: Target?, option: EffectOption, session: Session): ParticleObj {

            return Arcs(option.createContainer(target, session), option)
        }
    }

    class Arcs(val container: Target.Container, option: EffectOption) : ParticleObj(EffectSpawner(option)) {
        val step = Coerce.toDouble(option.demand.get(Effects.STEP, "10"))
        val radius = Coerce.toDouble(option.demand.get(Effects.RADIUS, "360"))
        val angle = Coerce.toDouble(option.demand.get(Effects.ANGLE, "0"))
        override fun show() {
            container.forEachLocation {
                Arc(toProxyLocation(), angle, radius, step, spawner).show()
            }
        }

    }

}
