package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerClickLeft : AbstractTimer<PlayerInteractEvent>() {
    override val name: String
        get() = "player click left"

    override val eventClazz: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun check(e: PlayerInteractEvent): ProxyCommandSender? {

        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
            return adaptPlayer(e.action)
        }
        return null
    }


}