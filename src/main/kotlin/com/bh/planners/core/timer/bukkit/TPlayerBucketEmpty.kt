package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBucketEmptyEvent

object TPlayerBucketEmpty : AbstractTimer<PlayerBucketEmptyEvent>() {
    override val name: String
        get() = "player bucked empty"
    override val eventClazz: Class<PlayerBucketEmptyEvent>
        get() = PlayerBucketEmptyEvent::class.java

    override fun check(e: PlayerBucketEmptyEvent): Target {
        return e.player.toTarget()
    }
}
