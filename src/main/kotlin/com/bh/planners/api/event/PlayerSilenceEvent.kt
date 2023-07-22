package com.bh.planners.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerSilenceEvent(val player: Player, val silence: Long) : BukkitProxyEvent()