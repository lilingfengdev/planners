package com.bh.planners.api.common

import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit

open class SimpleTimeoutTask(val tick: Long, open val closed: () -> Unit = EMPTY) {

    // 结束时间
    val end = System.currentTimeMillis() + tick * 50

    var isClosed = false

    companion object {

        val EMPTY = { }

        val cache = mutableListOf<SimpleTimeoutTask>()

        @Awake(LifeCycle.DISABLE)
        fun unregisterAll() {
            val millis = System.currentTimeMillis()
            cache.forEach {
                // 未执行任务
                if (millis < it.end) it.closed()
            }
        }

        fun cancel(simpleTask: SimpleTimeoutTask) {
            cache -= simpleTask
            // 如果已经结束了
            if (simpleTask.isClosed) return
            simpleTask.closed()
            simpleTask.isClosed = false
        }

        fun register(simpleTask: SimpleTimeoutTask, async: Boolean = !Bukkit.isPrimaryThread()) {
            cache += simpleTask
            submit(delay = simpleTask.tick, async = async) {
                Companion.cancel(simpleTask)
            }
        }

        fun createSimpleTask(tick: Long, async: Boolean = !Bukkit.isPrimaryThread(), closed: () -> Unit) {
            register(SimpleTimeoutTask(tick, closed), async)
        }

    }

}