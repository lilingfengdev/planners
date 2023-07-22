package com.bh.planners.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerGetExperienceEvent(val player: Player, var value: Int) : BukkitProxyEvent()