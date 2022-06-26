package com.bh.planners.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerLevelChangeEvent(val player: Player, val from: Int, val to: Int) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false


    val isUpgraded: Boolean
        get() = to > from


}