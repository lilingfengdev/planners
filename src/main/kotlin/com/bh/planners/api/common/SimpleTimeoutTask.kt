package com.bh.planners.api.common

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.submit

class SimpleTimeoutTask(tick: Long, val closed: () -> Unit) {

    val create = System.currentTimeMillis() + tick * 50

    companion object {
        val cache = mutableListOf<SimpleTimeoutTask>()

        @Awake(LifeCycle.DISABLE)
        fun unregisterAll() {
            val millis = System.currentTimeMillis()
            cache.forEach {
                // 未执行任务
                if (it.create > millis) it.closed()
            }
        }


        fun createSimpleTask(tick: Long, async: Boolean = !Bukkit.isPrimaryThread(), closed: () -> Unit) {
            val simpleTimeoutTask = SimpleTimeoutTask(tick, closed)
            cache += simpleTimeoutTask
            submit(delay = tick, async = async) {
                simpleTimeoutTask.closed()
            }
        }

    }

}