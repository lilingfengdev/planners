package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TPlayerCastSkillFailure : AbstractTimer<PlayerCastSkillEvents.Failure>() {

    override val name: String
        get() = "player cast skill failure"

    override val eventClazz: Class<PlayerCastSkillEvents.Failure>
        get() = PlayerCastSkillEvents.Failure::class.java

    override fun check(e: PlayerCastSkillEvents.Failure): Target {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerCastSkillEvents.Failure) {
        context.rootFrame().variables()["cause"] = e.result.name
        context.rootFrame().variables()["result"] = e.result.name
        context.rootFrame().variables()["skill"] = e.skill.key
    }


}