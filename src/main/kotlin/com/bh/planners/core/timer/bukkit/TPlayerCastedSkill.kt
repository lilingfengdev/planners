package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import taboolib.module.kether.ScriptContext

object TPlayerCastedSkill : AbstractTimer<PlayerCastSkillEvents.Post>() {

    override val name: String
        get() = "player casted skill"
    override val eventClazz: Class<PlayerCastSkillEvents.Post>
        get() = PlayerCastSkillEvents.Post::class.java

    override fun check(e: PlayerCastSkillEvents.Post): Target? {
        return e.player.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerCastSkillEvents.Post) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["skill"] = e.skill.key
    }

}
