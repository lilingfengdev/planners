package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.PlayerCastSkillEvents
import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerCastedSkill : AbstractTimer<PlayerCastSkillEvents.Post>() {

    override val name: String
        get() = "player casted skill"
    override val eventClazz: Class<PlayerCastSkillEvents.Post>
        get() = PlayerCastSkillEvents.Post::class.java

    override fun check(e: PlayerCastSkillEvents.Post): ProxyCommandSender? {
        return adaptPlayer(e.player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: PlayerCastSkillEvents.Post) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["skill"] = e.skill.key
    }

}
