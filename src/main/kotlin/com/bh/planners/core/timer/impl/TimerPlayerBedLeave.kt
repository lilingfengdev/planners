package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBedLeaveEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerBedLeave : AbstractTimer<PlayerBedLeaveEvent>() {
    override val name: String
        get() = "player bed leave"
    override val eventClazz: Class<PlayerBedLeaveEvent>
        get() = PlayerBedLeaveEvent::class.java

    override fun check(e: PlayerBedLeaveEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}
