package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerMoveEvent

object TimerPlayerBedLeave : AbstractTimer<PlayerBedLeaveEvent>() {
    override val name: String
        get() = "player bed leave"
    override val eventClazz: Class<PlayerBedLeaveEvent>
        get() = PlayerBedLeaveEvent::class.java

    override fun check(e: PlayerBedLeaveEvent): Player? {
        return e.player
    }
}
