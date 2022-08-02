package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerLevelChangeEvent
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

class TimerPlayerJobLevelUp : AbstractTimer<PlayerLevelChangeEvent>() {
    override val name: String
        get() = "player job level up"
    override val eventClazz: Class<PlayerLevelChangeEvent>
        get() = PlayerLevelChangeEvent::class.java

    override fun check(e: PlayerLevelChangeEvent): ProxyCommandSender? {
        if (e.to > e.from) return adaptPlayer(e.player)
        return null
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerLevelChangeEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["to"] = e.to
        context.rootFrame().variables()["from"] = e.from
    }

}