package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import taboolib.module.kether.ScriptContext

object TPlayerAttacked : AbstractTimerDamage() {
    override val name: String
        get() = "player attacked"

    override fun check(e: ProxyDamageEvent): Target? {
        return e.getPlayer(e.entity)?.toTarget()
    }


    /**
     * @Target 攻击目标
     * damager 攻击者的名称
     * entity 被攻击者的名称
     * cause 攻击原因
     * damage 攻击伤害
     */
    override fun onStart(context: ScriptContext, template: Template, e: ProxyDamageEvent) {
        super.onStart(context, template, e)
        if (e.damager is LivingEntity) {
            context.rootFrame().variables()["@Target"] = e.damager.toTarget()
        }
        context.rootFrame().variables()["damager"] = e.damager.name
        context.rootFrame().variables()["entity"] = e.entity.name
        context.rootFrame().variables()["cause"] = e.cause
        context.rootFrame().variables()["damage"] = e.damage
    }

}
