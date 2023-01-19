package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

object TPlayerClickLeft : AbstractTimer<PlayerInteractEvent>() {
    override val name: String
        get() = "player click left"

    override val eventClazz: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun check(e: PlayerInteractEvent): Target? {

        if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK && e.hand == EquipmentSlot.HAND) {
            return e.player.toTarget()
        }
        return null
    }


}