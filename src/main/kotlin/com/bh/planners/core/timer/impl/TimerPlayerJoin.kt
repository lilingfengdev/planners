package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent

object TimerPlayerJoin : AbstractTimer<PlayerJoinEvent>() {

    override val name: String
        get() = "player join"
    override val eventClazz: Class<PlayerJoinEvent>
        get() = PlayerJoinEvent::class.java

    override fun check(e: PlayerJoinEvent): Player? {
        return e.player
    }
}
