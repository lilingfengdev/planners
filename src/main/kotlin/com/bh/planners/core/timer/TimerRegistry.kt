package com.bh.planners.core.timer

import com.bh.planners.core.timer.impl.TimerRunnable
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.io.getInstance
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.registerBukkitListener

object TimerRegistry {

    private val map = mutableMapOf<String, Class<out Event>>()
    val triggers = mutableMapOf<String, Timer<*>>()


    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.LOAD)
    fun loadImplClass() {
        runningClasses.forEach {
            if (Timer::class.java.isAssignableFrom(it)) {
                val timer = it.getInstance()?.get() as? Timer<*> ?: return@forEach
                triggers[timer.name] = timer
                registerBukkitListener(timer.eventClazz,EventPriority.MONITOR,ignoreCancelled = false) {

                }
            }
        }
    }

    fun getTriggerByClass(clazz: Class<*>): String? {
        val entry = classes.entries.firstOrNull { it.value == clazz } ?: return null
        return entry.key
    }

    fun getClass(trigger: String): Class<Timer<*>>? {
        return classes[trigger]
    }

    @Awake(LifeCycle.LOAD)
    fun loadAll() {
        map["runnable"] = TimerRunnable.TimerRunnableEvent::class.java

    }

}
