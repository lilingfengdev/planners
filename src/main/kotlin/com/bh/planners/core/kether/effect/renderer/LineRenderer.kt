package com.bh.planners.core.kether.effect.renderer

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Effects
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.capture
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common5.Coerce

class LineRenderer(target: Target, container: Target.Container, option: EffectOption) : AbstractEffectRenderer(target, container,
    option
) {


    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))


    override fun sendTo(): Set<LivingEntity> {
        if (target !is Target.Location) return emptySet()

        val mutableSet = mutableSetOf<LivingEntity>()

        container.forEachLocation {
            val handler = Handler(target.value, this, option.step)
            handler.resetVector()
            var i = 0.0
            while (i < handler.length) {
                val vectorTemp: Vector = handler.vector!!.clone().multiply(i)
                option.spawn(target.value, handler.start.clone().add(vectorTemp).apply {
                    mutableSet.addAll(capture())
                })
                i += handler.step
            }
        }
        return mutableSet
    }

    class Handler(val start: Location, val end: Location, val step: Double) {

        var vector: Vector? = null
        var length: Double = 0.0

        fun resetVector() {
            this.vector = this.end.clone().subtract(this.start).toVector()
            this.length = this.vector!!.length()
            this.vector!!.normalize()
        }

    }

}