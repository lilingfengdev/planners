package com.bh.planners.core.skill.effect

import com.bh.planners.api.common.Demand
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.Target.Companion.createContainer
import com.bh.planners.core.skill.effect.common.Line
import taboolib.common.platform.function.info
import taboolib.common5.Coerce

/**
 * effect tie-line "pos1 [ -@dot 3,1 ] pos2 [ -@dot 4,100 ]"
 */
object EffectTieLine : Effect() {
    override val name: String
        get() = "tie-line"

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get("period", "0"))

    override fun sendTo(target: Target?, option: EffectOption, session: Session) {
        val pos1 = option.createContainer(target, session).getLocationTarget(0) ?: return
        val pos2 = option.createContainer(target, session).getLocationTarget(1) ?: return
        val period = option.period


        if (period <= 0) {
            Line.buildLine(pos1, pos2, option.step, EffectSpawner(option))
        } else {
            Line(pos1, pos2, option.step, period, EffectSpawner(option)).play()
        }

    }

}