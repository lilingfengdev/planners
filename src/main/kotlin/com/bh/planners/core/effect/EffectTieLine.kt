package com.bh.planners.core.effect

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.Line
import taboolib.common5.Coerce

/**
 * effect tie-line "pos1 [ -@dot 3,1 ] pos2 [ -@dot 4,100 ]"
 */
object EffectTieLine : com.bh.planners.core.effect.Effect() {
    override val name: String
        get() = "tie-line"

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get("period", "0"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context) {

        val effectSpawner = EffectSpawner(option)

        option.createContainer(target, context).thenAccept {
            val pos1 = it.getLocationTarget(0) ?: return@thenAccept
            val pos2 = it.getLocationTarget(1) ?: return@thenAccept
            val period = option.period
            if (period <= 0) {
                Line.buildLine(pos1, pos2, option.step, effectSpawner)
            } else {
                Line(pos1, pos2, option.step, period, effectSpawner).play()
            }
        }



    }

}