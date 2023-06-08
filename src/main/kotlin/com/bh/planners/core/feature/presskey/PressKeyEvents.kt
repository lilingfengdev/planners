package com.bh.planners.core.feature.presskey

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

object PressKeyEvents {


    class Get(val player: Player, val packet: Loader.Packet) : BukkitProxyEvent()

}