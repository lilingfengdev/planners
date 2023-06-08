package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.platform.util.isOffhand
import taboolib.platform.util.isRightClick

object TPlayerShieldLift : AbstractTimer<PlayerInteractEvent>() {

    override val name: String
        get() = "player shield lift"
    override val eventClazz: Class<PlayerInteractEvent>
        get() = PlayerInteractEvent::class.java

    override fun check(e: PlayerInteractEvent): Target? {
        return if (e.player.inventory.itemInMainHand.type.name.contains("SHIELD") && e.isRightClick() && e.isOffhand()) {
            return e.player.toTarget()
        } else null
    }

}