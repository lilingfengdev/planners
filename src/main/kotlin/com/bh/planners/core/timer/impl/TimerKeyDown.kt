package com.bh.planners.core.timer.impl

import com.bh.planners.core.feature.presskey.PressKeyEvents
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerKeyDown : AbstractTimer<PressKeyEvents.Get>() {
    override val name: String
        get() = "player key press"
    override val eventClazz: Class<PressKeyEvents.Get>
        get() = PressKeyEvents.Get::class.java

    override fun check(e: PressKeyEvents.Get): ProxyCommandSender? {
        if (e.packet.action == 1) {
            return adaptPlayer(e.player)
        }
        return null
    }

    override fun onStart(context: ScriptContext, template: Template, e: PressKeyEvents.Get) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["key"] = e.packet.key
    }
}