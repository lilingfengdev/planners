package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Arc
import taboolib.module.effect.Line
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectLine : EffectLoader<EffectLine.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "arc"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun sendTo(sender: Player, option: EffectOption): ParticleObj {
            return Lines(sender.location.toProxyLocation(), option.createContainer(sender), option)
        }
    }

    class Lines(val pos1: taboolib.common.util.Location, val container: Target.Container, option: EffectOption) :
        ParticleObj(EffectSpawner(option)) {

        val step = Coerce.toDouble(option.demand.get(Effects.STEP, "10"))

        override fun show() {
            container.forEachLocation { Line(pos1, this.toProxyLocation(), step, spawner) }
        }

    }

}
