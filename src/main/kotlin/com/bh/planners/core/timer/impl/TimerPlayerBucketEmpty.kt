package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerMoveEvent

object TimerPlayerBucketEmpty : AbstractTimer<PlayerBucketEmptyEvent>() {
    override val name: String
        get() = "player bucked empty"
    override val eventClazz: Class<PlayerBucketEmptyEvent>
        get() = PlayerBucketEmptyEvent::class.java

    override fun check(e: PlayerBucketEmptyEvent): Player? {
        return e.player
    }
}
