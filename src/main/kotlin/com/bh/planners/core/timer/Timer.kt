package com.bh.planners.core.timer

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.Event
import javax.script.ScriptContext

interface Timer<E : Event> {

    val eventClazz: Class<out Event>

    fun onStart(context: ScriptContext, template: Template)

    fun check(e: E): Player?

}
