package com.bh.planners.core.timer

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.Event
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptContext
import taboolib.module.kether.printKetherErrorMessage

abstract class AbstractTimer<E : Event>() : Timer<E> {

    protected abstract fun onStart(context: ScriptContext, e: E)

//    fun run(template: Template, e: E) {
//        if (template.script != null && template.script.isNotEmpty()) {
//            try {
//                submit(async = true) {
//                    onlinePlayers().forEach {
//                        KetherShell.eval(template.script, cacheScript = true, sender = it) {
//                            onStart(this, e)
//                        }
//                    }
//                }
//            } catch (e: Throwable) {
//                e.printKetherErrorMessage()
//                return
//            }
//        }
//    }


}
