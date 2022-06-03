package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.timer.AbstractTimer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerSelectedJob : AbstractTimer<PlayerSelectedJobEvent>() {
    override val name: String
        get() = "player select job"
    override val eventClazz: Class<PlayerSelectedJobEvent>
        get() = PlayerSelectedJobEvent::class.java

    override fun check(e: PlayerSelectedJobEvent): ProxyCommandSender? {
        return adaptPlayer(e.profile.player)
    }
}
