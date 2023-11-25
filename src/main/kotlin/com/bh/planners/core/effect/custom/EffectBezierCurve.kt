package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.parseTargetContainer
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common.platform.function.adaptLocation
import taboolib.module.effect.ParticleObj
import taboolib.module.effect.createNRankBezierCurve

object EffectBezierCurve : CustomEffect {
    override val name: String
        get() = "bezier"

    override fun getEffectObj(
        option: EffectOption,
        context: Context
    ): ParticleObj {

        val locations = mutableListOf<Location>()

        parseTargetContainer(option.origin, context).forEach {
            it.getLocation()?.let { loc -> locations.add(loc) }
        }

        val step = option.step
        val period = option.period

        return createNRankBezierCurve(locations.map { adaptLocation(it) }, step, period)

    }

}