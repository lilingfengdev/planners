package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.Template
import com.bh.planners.core.timer.Timer
import org.bukkit.event.player.PlayerToggleSneakEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerSneak : Timer<PlayerToggleSneakEvent> {
    override val name: String
        get() = "player sneak"

    override val eventClazz: Class<PlayerToggleSneakEvent>
        get() = PlayerToggleSneakEvent::class.java

    override fun check(e: PlayerToggleSneakEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    /**
     * isSneaking 是否在潜行
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleSneakEvent) {
        context.rootFrame().variables()["isSneaking"] = e.isSneaking
    }

}