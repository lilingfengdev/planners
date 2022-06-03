package com.bh.planners.core.kether.effect

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.api.particle.EffectSpawner
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.effect.Cube
import taboolib.module.effect.ParticleObj
import taboolib.platform.util.toProxyLocation

object EffectCube : EffectLoader<EffectCube.Impl>() {

    override val clazz: Class<Impl>
        get() = Impl::class.java

    override val name: String
        get() = "cube"


    class Impl(action: ParsedAction<*>) : Effect(action) {

        override fun sendTo(target: Target?, option: EffectOption, session: Session): ParticleObj {
            val container = option.createContainer(target, session)
            if (target !is Target.Location) return EFFECT_AIR
            return Cubes(target.value.toProxyLocation(), container, option)
        }
    }

    class Cubes(val pos1: taboolib.common.util.Location, val container: Target.Container, option: EffectOption) :
        ParticleObj(EffectSpawner(option)) {

        val step = Coerce.toDouble(option.demand.get(Effects.STEP, "10"))

        override fun show() {
            container.forEachLocation {
                Cube(pos1, this.toProxyLocation(), step, spawner).show()
            }
        }

    }

}
