package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.Template
import com.bh.planners.core.timer.Timer
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.event.player.PlayerToggleSprintEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerSpring : Timer<PlayerToggleSprintEvent> {

    override val name: String
        get() = "player sprint"

    override val eventClazz: Class<PlayerToggleSprintEvent>
        get() = PlayerToggleSprintEvent::class.java

    override fun check(e: PlayerToggleSprintEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleSprintEvent) {
        context.rootFrame().variables()["isSprinting"] = e.isSprinting
    }

}