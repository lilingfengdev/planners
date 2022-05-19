package com.bh.planners.core.timer

import org.bukkit.event.Event

inline fun <reified T> getTemplates(): List<T> {
    val trigger =
        TimerRegistry.getTriggerByClass(T::class.java) ?: error("Trigger '${T::class.java}' not registered.")
    return TimerDrive.templates.filter { trigger in it.triggers }.filterIsInstance<T>()
}
