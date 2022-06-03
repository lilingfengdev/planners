package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerBucketEmptyEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerBucketEmpty : AbstractTimer<PlayerBucketEmptyEvent>() {
    override val name: String
        get() = "player bucked empty"
    override val eventClazz: Class<PlayerBucketEmptyEvent>
        get() = PlayerBucketEmptyEvent::class.java

    override fun check(e: PlayerBucketEmptyEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}
