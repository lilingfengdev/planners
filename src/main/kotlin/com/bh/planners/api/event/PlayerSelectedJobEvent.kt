package com.bh.planners.api.event

import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.player.PlayerProfile
import taboolib.platform.type.BukkitProxyEvent

class PlayerSelectedJobEvent(val profile: PlayerProfile,val snapshot: Job? = null) : BukkitProxyEvent() {

    override val allowCancelled: Boolean
        get() = false

}
