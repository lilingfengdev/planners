package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.skill.effect.*
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.common.PlayerFrontCoordinate
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

open class CircleRenderer(target: Target, future: CompletableFuture<Target.Container>, option: EffectOption) : AbstractEffectRenderer(
    target, future, option
) {

    open val EffectOption.radius: Double
        get() = Coerce.toDouble(this.demand.get(Effects.RADIUS, "1.0"))

    open val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    val EffectOption.rotateX: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rx", "rotateX", "0")))

    val EffectOption.rotateY: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rx", "rotateX", "0")))

    val EffectOption.rotateZ: Double
        get() = Coerce.toDouble(this.demand.get(listOf("rx", "rotateX", "0")))

    // 粒子渲染周期间隔
    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get(listOf("period", "p"), "0"))


    override fun sendTo() {

        getContainer {
            forEachLocation {
                var i = 0.0

                if (option.period == 0L) {
                    while (i < 360.0) {
                        val radians = Math.toRadians(i)
                        val vector = Vector()
                        vector.x = option.radius * Math.cos(radians)
                        vector.z = option.radius * Math.sin(radians)
                        rotateVector(vector)
                        spawnParticle(location = this.clone().add(vector))
                        i += option.step
                    }
                } else {
                    submit(async = true, period = option.period) {

                        if (i >= 360) {
                            cancel()
                            return@submit
                        }

                        val radians = Math.toRadians(i)
                        val vector = Vector()
                        vector.x = option.radius * Math.cos(radians)
                        vector.z = option.radius * Math.sin(radians)
                        rotateVector(vector)
                        spawnParticle(location = this@forEachLocation.add(vector))
                        i += option.step
                    }
                }
            }
        }



    }

    fun rotateVector(vector: Vector) {
        if (this.option.rotateZ != 0.0) {
            rotateAroundAxisZ(vector, this.option.rotateZ)
        }
        if (this.option.rotateX != 0.0) {
            rotateAroundAxisZ(vector, this.option.rotateX)
        }
        if (this.option.rotateY != 0.0) {
            rotateAroundAxisZ(vector, this.option.rotateY)
        }
    }

}