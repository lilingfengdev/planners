package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.Projectile
import taboolib.module.kether.ScriptContext
import taboolib.platform.util.getMetaFirstOrNull

abstract class AbstractTimerDamage : AbstractTimer<ProxyDamageEvent>() {


    override fun onStart(context: ScriptContext, template: Template, e: ProxyDamageEvent) {
        super.onStart(context, template, e)
        if (e.damager is Projectile) {
            context.rootFrame().variables()["projectile"] = e.damager
        }
        context.rootFrame().variables()["isSkillDamage"] = e.entity.getMetaFirstOrNull("Planners:Attack")?.asBoolean() == true

        context.rootFrame().variables()["entity"] = e.entity
        context.rootFrame().variables()["cause"] = e.cause
        context.rootFrame().variables()["damage"] = e.damage
    }


}