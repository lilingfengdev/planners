package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerToggleFlightEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerToggleFlight : AbstractTimer<PlayerToggleFlightEvent>() {
    override val name: String
        get() = "player toggle flight"
    override val eventClazz: Class<PlayerToggleFlightEvent>
        get() = PlayerToggleFlightEvent::class.java

    override fun check(e: PlayerToggleFlightEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleFlightEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["isFlying"] = e.isFlying
    }

}