package com.bh.planners.api.event

import com.bh.planners.core.pojo.key.IKeySlot
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerKeydownEvent(val player: Player, val keySlot: IKeySlot) : BukkitProxyEvent()
