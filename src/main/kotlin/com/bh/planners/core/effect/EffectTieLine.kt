package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.common.Line
import com.bh.planners.core.kether.game.ActionEffect
import taboolib.common5.Coerce

/**
 * effect tie-line "pos1 [ -@dot 3,1 ] pos2 [ -@dot 4,100 ]"
 */
object EffectTieLine : Effect() {
    override val name: String
        get() = "tie-line"

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

        val effectSpawner = EffectSpawner(option)
        val period = option.period
        val step = option.step

        option.createContainer(target, context).thenAccept {
            val pos1 = it.getLocationTarget(0) ?: return@thenAccept
            val pos2 = it.getLocationTarget(1) ?: return@thenAccept

            val builder = EffectLine.Builder(pos1, pos2, step, effectSpawner)
            ParticleFrame.create(period,builder, response)

        }


    }

}