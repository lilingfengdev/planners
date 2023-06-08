package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TPlayerJobLevelUp : AbstractTimer<PlayerLevelChangeEvent>() {
    override val name: String
        get() = "player job level up"
    override val eventClazz: Class<PlayerLevelChangeEvent>
        get() = PlayerLevelChangeEvent::class.java

    override fun check(e: PlayerLevelChangeEvent): Target? {
        if (e.to > e.from) return e.player.toTarget()
        return null
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerLevelChangeEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["to"] = e.to
        context.rootFrame().variables()["from"] = e.from
    }

}