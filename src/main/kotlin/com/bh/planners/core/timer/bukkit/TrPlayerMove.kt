package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerMoveEvent

object TrPlayerMove : AbstractTimer<PlayerMoveEvent>() {
    override val name: String
        get() = "player move"
    override val eventClazz: Class<PlayerMoveEvent>
        get() = PlayerMoveEvent::class.java

    override fun check(e: PlayerMoveEvent): Target {
        return e.player.toTarget()
    }
}
