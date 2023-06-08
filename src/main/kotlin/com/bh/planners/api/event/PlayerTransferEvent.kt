package com.bh.planners.api.event

import com.bh.planners.core.pojo.Job
import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class PlayerTransferEvent(val player: Player, val target: Job) : BukkitProxyEvent() {


}