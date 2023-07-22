package com.bh.planners.core.timer.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.event.player.PlayerToggleFlightEvent
import taboolib.module.kether.ScriptContext

object TToggleFlight : AbstractTimer<PlayerToggleFlightEvent>() {
    override val name: String
        get() = "player toggle flight"
    override val eventClazz: Class<PlayerToggleFlightEvent>
        get() = PlayerToggleFlightEvent::class.java

    override fun check(e: PlayerToggleFlightEvent): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerToggleFlightEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["isFlying"] = e.isFlying
    }

}