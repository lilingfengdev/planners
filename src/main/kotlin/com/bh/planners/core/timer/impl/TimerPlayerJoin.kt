package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerJoin : AbstractTimer<PlayerJoinEvent>() {

    override val name: String
        get() = "player join"
    override val eventClazz: Class<PlayerJoinEvent>
        get() = PlayerJoinEvent::class.java

    override fun check(e: PlayerJoinEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}
