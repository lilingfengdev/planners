package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBedEnterEvent

object TPlayerBedEnter : AbstractTimer<PlayerBedEnterEvent>() {
    override val name: String
        get() = "player bed enter"
    override val eventClazz: Class<PlayerBedEnterEvent>
        get() = PlayerBedEnterEvent::class.java

    override fun check(e: PlayerBedEnterEvent): Target? {
        return e.player.toTarget()
    }
}
