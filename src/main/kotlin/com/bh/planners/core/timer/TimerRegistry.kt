package com.bh.planners.core.timer

import com.bh.planners.core.timer.impl.TimerRunnable
import org.bukkit.event.Event
import taboolib.common.LifeCycle
import taboolib.common.io.runningClasses
import taboolib.common.platform.Awake

object TimerRegistry {

    private val map = mutableMapOf<String, Class<out Event>>()
    val classes = mutableMapOf<String, CTimer>()


    @Suppress("UNCHECKED_CAST")
    @Awake(LifeCycle.LOAD)
    fun loadImplClass() {
        runningClasses.forEach {
            if (Timer::class.java.isAssignableFrom(it) && it.isAnnotationPresent(Registered::class.java)) {
                val registered = it.getAnnotation(Registered::class.java)
                val clazz = it as Class<Timer<*>>
                classes[registered.name] = clazz
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
