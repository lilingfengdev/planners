package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerJumpEvent
import com.bh.planners.core.timer.AbstractTimer
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer

object TimerPlayerJump : AbstractTimer<PlayerJumpEvent>() {
    override val name: String
        get() = "player jump"

    override val eventClazz: Class<PlayerJumpEvent>
        get() = PlayerJumpEvent::class.java

    override fun check(e: PlayerJumpEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }
}