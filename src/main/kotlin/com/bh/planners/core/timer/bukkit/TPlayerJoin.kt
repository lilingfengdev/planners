package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerJoinEvent

object TPlayerJoin : AbstractTimer<PlayerJoinEvent>() {

    override val name: String
        get() = "player join"
    override val eventClazz: Class<PlayerJoinEvent>
        get() = PlayerJoinEvent::class.java

    override fun check(e: PlayerJoinEvent): Target {
        return e.player.toTarget()
    }
}
