package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerClickRight : AbstractTimer<PlayerInteractEvent>() {
    override val name: String
        get() = "player click right"

    override val eventClazz: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun check(e: PlayerInteractEvent): ProxyCommandSender? {

        if (e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK && e.hand == EquipmentSlot.HAND) {
            return adaptPlayer(e.player)
        }
        return null
    }


}