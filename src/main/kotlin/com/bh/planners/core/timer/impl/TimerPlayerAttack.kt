package com.bh.planners.core.timer.impl

import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerAttack : AbstractTimer<EntityDamageByEntityEvent>() {
    override val name: String
        get() = "player attack"
    override val eventClazz: Class<EntityDamageByEntityEvent>
        get() = EntityDamageByEntityEvent::class.java

    override fun check(e: EntityDamageByEntityEvent): ProxyCommandSender? {
        val player = e.damager as? Player ?: return null
        return adaptPlayer(player)
    }

    override fun onStart(context: ScriptContext, template: Template, e: EntityDamageByEntityEvent) {
        super.onStart(context, template, e)
        context.rootFrame().variables()["damager"] = e.damager.name
        context.rootFrame().variables()["entity"] = e.entity.name
        context.rootFrame().variables()["cause"] = e.cause
        context.rootFrame().variables()["damage"] = e.damage
    }

}
