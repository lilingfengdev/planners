package com.bh.planners.core.effect.renderer

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.effect.*
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.common.Line
import com.bh.planners.util.entityAt
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

class LineRenderer(
    target: Target,
    future: CompletableFuture<Target.Container>,
    option: EffectOption,
    val context: Context
) :
    AbstractEffectRenderer(
        target, future, option
    ) {

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))

    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get("period", "0"))

    val EffectOption.time: Long
        get() = Coerce.toLong(this.demand.get(listOf("time", "t"), "20"))

    override fun sendTo() {
        if (target !is Target.Location) return
        getContainer {
            val property = target.getProperty<LivingEntity>("livingEntity")
            forEachLocation {

                if (option.period <= 0) {

                    Line.buildLine(target.value, this, option.step, EffectSpawner(option))

                } else {

                    val line = Line(target.value, this, option.step, period = option.period, spawner)
                    if (context is Session) {
                        line.play()
                    } else {
                        line.callPlay { task ->
                            val entityAt = this.entityAt().apply { remove(property) }
                            if (entityAt.isNotEmpty()) {
                                task.cancel()
                            }
                        }
                    }

                }

            }
        }
    }

}