package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Arc
import taboolib.module.effect.Circle
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectCircle : EffectLoader<EffectCircle.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "arc"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun sendTo(sender: Player, option: EffectOption): ParticleObj {
            val step = Coerce.toDouble(option.demand.get(step, "10"))
            val radius = Coerce.toDouble(option.demand.get(radius, "360"))
            return Circle(sender.location.toProxyLocation(), radius, step, EffectSpawner(option))
        }
    }
}
