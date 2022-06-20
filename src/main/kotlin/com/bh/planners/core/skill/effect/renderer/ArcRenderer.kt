package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.Effects
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.common.PlayerFrontCoordinate
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import kotlin.math.cos
import kotlin.math.sin

open class ArcRenderer(target: Target, container: Target.Container, option: EffectOption) : AbstractEffectRenderer(
    target, container, option
) {

    open val EffectOption.startAngle: Double
        get() = Coerce.toDouble(this.demand.get("start", "0.0"))

    open val EffectOption.angle: Double
        get() = Coerce.toDouble(this.demand.get(Effects.ANGLE, "0.0"))

    open val EffectOption.radius: Double
        get() = Coerce.toDouble(this.demand.get(Effects.RADIUS, "0.0"))

    open val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))

    // 倾斜
    open val EffectOption.slope: Double
        get() = Coerce.toDouble(this.demand.get(listOf("slope", "slope"), "0.0"))

    // 粒子渲染周期间隔
    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get(listOf("period", "p"), "0"))


    override fun sendTo() {

        container.forEachLocation {

            var i = 0.0
            val coordinate = PlayerFrontCoordinate(this)
            submit(async = true, period = option.period) {
                if (i > option.angle) {
                    cancel()
                    return@submit
                }
                val radians = Math.toRadians(i + option.startAngle)
                val x: Double = option.radius * cos(radians)
                val z: Double = option.radius * sin(radians)
                val loc = coordinate.newLocation(x, i * option.slope, z)
                spawnParticle(loc, loc)
                i += option.step
            }

        }
    }

}