package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerMoveEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerMove : AbstractTimer<PlayerMoveEvent>() {
    override val name: String
        get() = "player move"
    override val eventClazz: Class<PlayerMoveEvent>
        get() = PlayerMoveEvent::class.java

    override fun check(e: PlayerMoveEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}
