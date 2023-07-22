package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.Template
import com.bh.planners.core.timer.Timer
import org.bukkit.event.player.PlayerToggleSprintEvent
import taboolib.module.kether.ScriptContext

object TPlayerSpring : Timer<PlayerToggleSprintEvent> {

    override val name: String
        get() = "player sprint"

    override val eventClazz: Class<PlayerToggleSprintEvent>
        get() = PlayerToggleSprintEvent::class.java

    override fun check(e: PlayerToggleSprintEvent): Target {
        return e.player.toTarget()
    }

    /**
     * isSprinting 是否在疾跑
     */
    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleSprintEvent) {
        context.rootFrame().variables()["isSprinting"] = e.isSprinting
    }

}