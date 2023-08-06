package com.bh.planners.api.event

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent
import com.bh.planners.core.pojo.chant.Process

class PlayerChantEvents {

    class Start(val player: Player, val process: Process) : BukkitProxyEvent()

    class Stop(val player: Player, val process: Process) : BukkitProxyEvent() {

        val type = if (process.actionBreak) {
            StopType.INTERRUPTED
        } else {
            StopType.NATURAL
        }

        val isNatural: Boolean
            get() = type == StopType.NATURAL

        val isInterrupted: Boolean
            get() = type == StopType.INTERRUPTED

    }

    enum class StopType {
        NATURAL, INTERRUPTED
    }

}