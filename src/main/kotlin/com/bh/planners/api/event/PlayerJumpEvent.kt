package com.bh.planners.api.event

import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.event.PlayerJumpEvent
import taboolib.platform.type.BukkitProxyEvent

class PlayerJumpEvent(val player: Player) : BukkitProxyEvent() {


    companion object {

        @SubscribeEvent(ignoreCancelled = false)
        fun e(e: PlayerJumpEvent) {
            com.bh.planners.api.event.PlayerJumpEvent(e.player).call()
        }

    }

}