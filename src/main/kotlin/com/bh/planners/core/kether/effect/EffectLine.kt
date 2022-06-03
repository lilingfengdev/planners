package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
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

        override fun sendTo(target: Target?, option: EffectOption, session: Session): ParticleObj {

            if (target !is Target.Location) return EFFECT_AIR

            return Lines(target, option.createContainer(target, session), option)
        }
    }

    class Lines(val target: Target.Location, val container: Target.Container, option: EffectOption) :
        ParticleObj(EffectSpawner(option)) {

        val step = Coerce.toDouble(option.demand.get(Effects.STEP, "10"))

        override fun show() {

            container.forEachLocation {
                Line(target.value.toProxyLocation(), this.toProxyLocation(), step, spawner).show()
            }
        }

    }

}
