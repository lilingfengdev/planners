package com.bh.planners.api.event

import com.bh.planners.core.pojo.player.PlayerProfile
import taboolib.platform.type.BukkitProxyEvent

class PlayerProfileLoadEvent(val profile: PlayerProfile) : BukkitProxyEvent()