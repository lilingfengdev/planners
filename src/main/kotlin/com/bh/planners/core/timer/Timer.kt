package com.bh.planners.core.timer

import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

interface Timer<E : Event> {

    val name: String

    val eventClazz: Class<E>

    fun onStart(context: ScriptContext, template: Template, e: E)

    fun check(e: E): Player?

}
