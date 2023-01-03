package com.bh.planners.core.timer.impl

import com.bh.planners.api.ContextAPI
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.platform.util.isOffhand
import taboolib.platform.util.isRightClick

object TimerPlayerShieldLift : AbstractTimer<PlayerInteractEvent>() {

    override val name: String
        get() = "player shield lift"
    override val eventClazz: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java
    override fun check(e: PlayerInteractEvent): ProxyCommandSender? {
        return if (e.player.inventory.itemInMainHand.type.name.contains("SHIELD") && e.isRightClick() && e.isOffhand()) {
            return ContextAPI.createProxy(e.player)
        } else null
    }

}