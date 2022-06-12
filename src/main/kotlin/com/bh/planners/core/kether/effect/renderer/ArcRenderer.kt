package com.bh.planners.core.kether.effect.renderer

import com.bh.planners.api.particle.EffectOption
import com.bh.planners.core.kether.effect.Effects
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.capture
import org.bukkit.entity.LivingEntity
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin

open class ArcRenderer(target: Target, container: Target.Container, option: EffectOption) : AbstractEffectRenderer(target, container,
    option
) {

    open val EffectOption.startAngle: Double
        get() = Coerce.toDouble(this.demand.get("start", "0.0"))

    open val EffectOption.angle: Double
        get() = Coerce.toDouble(this.demand.get(Effects.ANGLE, "0.0"))

    open val EffectOption.radius: Double
        get() = Coerce.toDouble(this.demand.get(Effects.RADIUS, "0.0"))

    open val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))


    override fun sendTo(): Set<LivingEntity> {
        val mutableSet = mutableSetOf<LivingEntity>()

        container.forEachLocation {
            var i: Double = option.startAngle
            while (i < option.angle) {
                val radians = Math.toRadians(i)
                val x: Double = option.radius * cos(radians)
                val z: Double = option.radius * sin(radians)
                option.spawn(this, this.clone().add(x, 0.0, z).apply {
                    mutableSet.addAll(capture())
                })
                i += option.step
            }
        }
        return mutableSet
    }

}