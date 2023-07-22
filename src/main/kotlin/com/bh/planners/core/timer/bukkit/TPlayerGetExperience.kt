package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerGetExperienceEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TPlayerGetExperience : AbstractTimer<PlayerGetExperienceEvent>() {
    override val name: String
        get() = "player get exp"

    override val eventClazz: Class<PlayerGetExperienceEvent>
        get() = PlayerGetExperienceEvent::class.java

    override fun check(e: PlayerGetExperienceEvent): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerGetExperienceEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["value"] = e.value
    }

}