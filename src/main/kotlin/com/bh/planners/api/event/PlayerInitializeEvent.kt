package com.bh.planners.api.event

import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerInitializeEvent(val player: Player, val profile: PlayerProfile) : BukkitProxyEvent()