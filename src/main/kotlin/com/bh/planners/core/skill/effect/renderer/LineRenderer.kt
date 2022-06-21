package com.bh.planners.core.skill.effect.renderer

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

class LineRenderer(target: Target, container: Target.Container, option: EffectOption, val session: Session) :
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
            var currentTime = 0L
            container.forEachEntity {
                val handler = Handler(target.value, this.eyeLocation)
                submit(async = true, period = option.period) {

                    if (currentTime >= option.time) {
                        cancel()
                        return@submit
                    }

                    handler.end = this@forEachEntity.eyeLocation
                    handler.resetVector()
                    val vector = handler.vector!!.clone().multiply(option.step)
                    handler.start = handler.start.clone().add(vector)
                    spawnParticle(target.value, handler.start)
                    currentTime += option.period
                    if (handler.start.distance(handler.end) < 1.0) {
                        cancel()
                        session.callEvent(option.onCapture ?: return@submit, Capture(this@forEachEntity))
                    }

                }
            }

        } else {
            val property = target.getProperty<LivingEntity>("livingEntity")
            container.forEachLocation {

                if (option.period <= 0) {
                    Line.buildLine(target.value, this, option.step, EffectSpawner(option))

                    val entityAt = this.entityAt().apply { remove(property) }
                    session.callEvent(option.onCapture ?: return@forEachLocation, Capture(entityAt.first()))
                } else {

                    val handler = Handler(target.value, this)
                    handler.resetVector()
                    var i = 0.0
                    submit(async = true, period = option.period) {
                        if (i > handler.length) {
                            cancel()
                            return@submit
                        }
                        val vectorTemp = handler.vector!!.clone().multiply(i)
                        val location = handler.start.clone().add(vectorTemp)
                        spawnParticle(location = location)
                        val entityAt = location.entityAt().apply { remove(property) }
                        // 如果捕获到实体 立刻停止
                        if (entityAt.isNotEmpty()) {
                            cancel()
                            session.callEvent(option.onCapture ?: return@submit, Capture(entityAt.first()))
                            return@submit
                        }
                        i += option.step
                    }
                }

            }
        }

        fun getLocationEntity(location: Location): LivingEntity? {
            return location.entityAt().firstOrNull()
        }


//        container.forEachLocation {
//            val handler = Handler(target.value, this, option.step)
//            handler.resetVector()
//            var i = 0.0
//            while (i < handler.length) {
//                val vectorTemp: Vector = handler.vector!!.clone().multiply(i)
//                option.spawn(target.value, handler.start.clone().add(vectorTemp))
//                i += handler.step
//            }
//        }
    }

    class Handler(var start: Location, var end: Location) {

        var vector: Vector? = null
        var length: Double = 0.0


        fun resetVector() {
            this.vector = this.end.clone().subtract(this.start).toVector()
            this.length = this.vector!!.length()
            this.vector!!.normalize()
        }

    }

}