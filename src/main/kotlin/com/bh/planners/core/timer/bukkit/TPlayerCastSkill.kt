package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TPlayerCastSkill : AbstractTimer<PlayerCastSkillEvents.Pre>() {

    override val name: String
        get() = "player cast skill"
    override val eventClazz: Class<PlayerCastSkillEvents.Pre>
        get() = PlayerCastSkillEvents.Pre::class.java

    override fun check(e: PlayerCastSkillEvents.Pre): Target? {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerCastSkillEvents.Pre) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["skill"] = e.skill.key
    }

}
