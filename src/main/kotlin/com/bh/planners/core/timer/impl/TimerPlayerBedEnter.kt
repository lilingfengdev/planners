package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerMoveEvent

object TimerPlayerBedEnter : AbstractTimer<PlayerBedEnterEvent>() {
    override val name: String
        get() = "player bed enter"
    override val eventClazz: Class<PlayerBedEnterEvent>
        get() = PlayerBedEnterEvent::class.java

    override fun check(e: PlayerBedEnterEvent): Player? {
        return e.player
    }
}
