package com.bh.planners.api.common

import com.bh.planners.core.effect.EffectSpawner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import org.bukkit.Bukkit
import org.bukkit.Location
import com.bh.planners.api.common.scheduler.SynchronizationContext
import com.bh.planners.api.common.scheduler.scope
import com.bh.planners.core.kether.game.ActionEffect
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


    companion object {

        fun create(time: String, builder: Builder,response: ActionEffect.Response, task: ParticleFrame.() -> Unit = {}): ParticleFrame {

            val duration = if (time.endsWith("s")) {
                Coerce.toLong(Coerce.toDouble(time.substring(0, time.lastIndex)) * 1000)
            } else if (time.endsWith("t")) {
                Coerce.toLong(time.substring(0, time.lastIndex)) * 50
            } else {
                Coerce.toLong(time) * 50
            }

            val frame = ParticleFrame(duration) {
                val next = builder.next()
                if (next == null) {
                    close()
                    return@ParticleFrame
                }
                builder.spawner.spawn(next)
                response.onTick(next)
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

        abstract fun next(): Location?

    }

}