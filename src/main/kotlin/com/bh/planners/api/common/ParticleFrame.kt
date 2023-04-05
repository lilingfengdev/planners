package com.bh.planners.api.common

import com.bh.planners.core.effect.EffectSpawner
import com.bh.planners.core.effect.Target
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import org.bukkit.Location
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.submitAsync
import taboolib.common5.Coerce
import taboolib.platform.BukkitPlugin

class ParticleFrame(val duration: Long, val task: ParticleFrame.() -> Unit) : Runnable {

    var closed = false
        private set

    fun close() {
        closed = true
    }

    fun hasNext(): Boolean {
        return !closed
    }

    /**
     * Thread sleep 不要问为什么 问就是没有好的解决方案
     * 如果你有好的解决方案 欢迎商讨
     */
    override fun run() {
        submitAsync {
            while (hasNext()) {
                task()
                if (duration > 0) {
                    Thread.sleep(duration)
                }
            }
        }
    }

    class FrameBuilder {

        lateinit var time: String
        lateinit var builder: Builder
        var response: ActionEffect.Response = ActionEffect.Response(object : Context.SourceImpl(Target.consoleTarget) {

            override val sourceId: String
                get() = "EMPTY"

        }, emptyMap())
        var task: ParticleFrame.() -> Unit = {}

        fun time(time: String) {
            this.time = time
        }

        fun builder(builder: Builder) {
            this.builder = builder
        }

        fun response(response: ActionEffect.Response) {
            this.response = response
        }

        fun task(task: ParticleFrame.() -> Unit) {
            this.task = task
        }

    }

    companion object {

        val cancelableList = CancelableList()

        fun FrameBuilder.new(block: FrameBuilder.() -> Unit): FrameBuilder {
            return FrameBuilder().apply(block)
        }

        fun create(builder: FrameBuilder): ParticleFrame {
            return create(builder.time, builder.builder, builder.response, builder.task)
        }

        fun create(time: String, builder: Builder, response: ActionEffect.Response, task: ParticleFrame.() -> Unit = {}): ParticleFrame {

            val duration = if (time.endsWith("s")) {
                Coerce.toLong(Coerce.toDouble(time.substring(0, time.lastIndex)) * 1000)
            } else if (time.endsWith("t")) {
                Coerce.toLong(time.substring(0, time.lastIndex)) * 50
            } else {
                Coerce.toLong(time) * 50
            }

            val frame = ParticleFrame(duration) {

                // 单点渲染
                if (builder.isSingle) {
                    val next = builder.next()
                    if (next == null) {
                        close()
                        return@ParticleFrame
                    }
                    builder.spawner.spawn(next)
                    response.handleTick(next)
                }
                // 多点渲染
                else {
                    val nexts = builder.nexts()
                    if (nexts == cancelableList) {
                        close()
                        return@ParticleFrame
                    }
                    nexts.forEach { builder.spawner.spawn(it) }
                    response.handleTick(nexts)
                }

                task()


            }

            if (builder.run) {
                frame.run()
            }

            return frame
        }

    }

    abstract class Builder(val spawner: EffectSpawner) {

        open val run = true

        val isSingle = true

        abstract fun next(): Location?

        fun nexts(): List<Location> {
            return emptyList()
        }

        fun cancel() = cancelableList

    }

    class CancelableList : ArrayList<Location>()

}