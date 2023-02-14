package com.bh.planners.core.timer

import com.bh.planners.core.effect.Target
import org.bukkit.event.Event
import taboolib.common.platform.event.EventPriority
import taboolib.module.kether.ScriptContext

interface Timer<E : Event> {

    val name: String

    val ignoreCancelled : Boolean
        get() = false

    val priority : EventPriority
        get() = EventPriority.MONITOR

    val eventClazz: Class<E>

    fun onStart(context: ScriptContext, template: Template, e: E)

    fun check(e: E): Target?

    fun condition(template: Template,event: E) : Boolean {
        return true
    }

}
