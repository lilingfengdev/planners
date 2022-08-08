package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerGetExperienceEvent
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerGetExperience : AbstractTimer<PlayerGetExperienceEvent>() {
    override val name: String
        get() = "player get exp"

    override val eventClazz: Class<PlayerGetExperienceEvent>
        get() = PlayerGetExperienceEvent::class.java

    override fun check(e: PlayerGetExperienceEvent): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerGetExperienceEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["value"] = e.value
    }

}