package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import taboolib.module.kether.ScriptContext
import taboolib.platform.util.attacker

object TPlayerAttackPre : AbstractTimerDamage() {

    override val name: String
        get() = "player attack pre"

    override fun check(e: ProxyDamageEvent): Target? {
        return e.getPlayer(e.event?.attacker ?: return null)?.toTarget()
    }

    override fun onStart(context: ScriptContext, template: Template, e: ProxyDamageEvent) {
        super.onStart(context, template, e)
        if (e.damager is LivingEntity) {
            context.rootFrame().variables()["@Target"] = e.damager.toTarget()
        }
    }


}