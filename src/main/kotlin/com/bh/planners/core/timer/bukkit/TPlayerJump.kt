package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerJumpEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer

object TPlayerJump : AbstractTimer<PlayerJumpEvent>() {
    override val name: String
        get() = "player jump"

    override val eventClazz: Class<PlayerJumpEvent>
        get() = PlayerJumpEvent::class.java

    override fun check(e: PlayerJumpEvent): Target {
        return e.player.toTarget()
    }
}