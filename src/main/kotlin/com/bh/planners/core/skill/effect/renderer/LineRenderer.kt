package com.bh.planners.core.skill.effect.renderer

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.skill.effect.*
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.common.Line
import com.bh.planners.core.skill.effect.inline.Capture
import com.bh.planners.core.skill.effect.inline.InlineEvent.Companion.callEvent
import com.bh.planners.util.entityAt
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import taboolib.common.platform.function.submit
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common5.Coerce

class LineRenderer(target: Target, container: Target.Container, option: EffectOption, val context: Context) :
    AbstractEffectRenderer(
        target, container, option
    ) {

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "0.0"))

    val EffectOption.period: Long
        get() = Coerce.toLong(this.demand.get("period", "0"))

    val EffectOption.time: Long
        get() = Coerce.toLong(this.demand.get(listOf("time", "t"), "20"))

    val EffectOption.lock: Boolean
        get() = this.demand.has("lock")

    val EffectOption.onCapture: String?
        get() = demand.get("onCapture")


    override fun sendTo() {
        if (target !is Target.Location) return

        // 追踪目标
        // 两个参数 period 和
        if (option.lock) {
            container.forEachEntity {
                var currentTime = 0L
                val line = Line(target.value, this.eyeLocation, option.step, period = option.period, spawner)
                line.callPlay { task ->
                    if (currentTime > option.time) {
                        task.cancel()
                        return@callPlay
                    }
                    line.setStart(location)
                    line.setEnd(this@forEachEntity.eyeLocation)
                    currentTime += option.period
                    if (this.distance(this@forEachEntity.eyeLocation) < 1.0 && context is Session) {
                        task.cancel()
                        context.callEvent(option.onCapture ?: return@callPlay, Capture(this@forEachEntity))
                    }
                }
            }
        } else {
            val property = target.getProperty<LivingEntity>("livingEntity")
            container.forEachLocation {

                if (option.period <= 0) {
                    Line.buildLine(target.value, this, option.step, EffectSpawner(option))
                    if (context is Session) {
                        val entityAt = this.entityAt().apply { remove(property) }
                        context.callEvent(option.onCapture ?: return@forEachLocation, Capture(entityAt.first()))
                    }
                } else {
                    val line = Line(target.value, this, option.step, period = option.period, spawner)
                    if (context is Session) {
                        line.play()
                    } else {
                        line.callPlay { task ->
                            val entityAt = this.entityAt().apply { remove(property) }
                            if (entityAt.isNotEmpty()) {
                                task.cancel()
                                (context as Session).callEvent(option.onCapture ?: return@callPlay, Capture(entityAt.first()))
                            }
                        }
                    }

                }

            }
        }
    }

}