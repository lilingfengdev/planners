package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer

object TPlayerSelectedJob : AbstractTimer<PlayerSelectedJobEvent>() {
    override val name: String
        get() = "player select job"
    override val eventClazz: Class<PlayerSelectedJobEvent>
        get() = PlayerSelectedJobEvent::class.java

    override fun check(e: PlayerSelectedJobEvent): Target? {
        return e.profile.player.toTarget()
    }
}
