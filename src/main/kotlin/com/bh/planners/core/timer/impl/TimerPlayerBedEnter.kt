package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBedEnterEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerBedEnter : AbstractTimer<PlayerBedEnterEvent>() {
    override val name: String
        get() = "player bed enter"
    override val eventClazz: Class<PlayerBedEnterEvent>
        get() = PlayerBedEnterEvent::class.java

    override fun check(e: PlayerBedEnterEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}
