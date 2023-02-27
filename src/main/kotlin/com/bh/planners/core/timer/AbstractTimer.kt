package com.bh.planners.core.timer

import com.bh.planners.api.common.Plugin
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.Event
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

@Plugin("@Abstract")
abstract class AbstractTimer<E : Event>() : Timer<E> {

    override fun onStart(context: ScriptContext, template: Template, e: E) {
        context.rootFrame().variables()["id"] = template.id
    }

}
