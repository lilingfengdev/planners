package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent

object TimerPlayerMove : AbstractTimer<PlayerMoveEvent>() {
    override val name: String
        get() = "player move"
    override val eventClazz: Class<PlayerMoveEvent>
        get() = PlayerMoveEvent::class.java

    override fun check(e: PlayerMoveEvent): Player? {
        return e.player
    }
}
