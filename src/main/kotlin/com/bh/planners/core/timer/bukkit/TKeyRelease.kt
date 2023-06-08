package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.feature.presskey.PressKeyEvents
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TKeyRelease : AbstractTimer<PressKeyEvents.Get>() {
    override val name: String
        get() = "player key release"
    override val eventClazz: Class<PressKeyEvents.Get>
        get() = PressKeyEvents.Get::class.java

    override fun check(e: PressKeyEvents.Get): Target? {
        if (e.packet.action == 1) {
            return e.player.toTarget()
        }
        return null
    }

    override fun onStart(context: ScriptContext, template: Template, e: PressKeyEvents.Get) {
        super.onStart(context, template, e)

        context.rootFrame().variables()["key"] = e.packet.key
    }
}