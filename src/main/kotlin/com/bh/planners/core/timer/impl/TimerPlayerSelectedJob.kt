package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player

object TimerPlayerSelectedJob : AbstractTimer<PlayerSelectedJobEvent>() {
    override val name: String
        get() = "player select job"
    override val eventClazz: Class<PlayerSelectedJobEvent>
        get() = PlayerSelectedJobEvent::class.java

    override fun check(e: PlayerSelectedJobEvent): Player? {
        return e.profile.player
    }
}
