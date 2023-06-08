package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBedLeaveEvent

object TPlayerBedLeave : AbstractTimer<PlayerBedLeaveEvent>() {
    override val name: String
        get() = "player bed leave"
    override val eventClazz: Class<PlayerBedLeaveEvent>
        get() = PlayerBedLeaveEvent::class.java

    override fun check(e: PlayerBedLeaveEvent): Target? {
        return e.player.toTarget()
    }
}
