package com.bh.planners.core.timer

import com.bh.planners.api.common.Plugin
import org.bukkit.event.Event
import taboolib.module.kether.ScriptContext

@Plugin("@Abstract")
abstract class AbstractTimer<E : Event>() : Timer<E> {

    override fun onStart(context: ScriptContext, template: Template, e: E) {
        context.rootFrame().variables()["id"] = template.id
    }

}
