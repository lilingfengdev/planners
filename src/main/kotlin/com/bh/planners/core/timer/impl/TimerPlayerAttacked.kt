package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.ScriptContext

object TimerPlayerAttacked : AbstractTimer<ProxyDamageEvent>() {
    override val name: String
        get() = "player attacked"
    override val eventClazz: Class<ProxyDamageEvent>
        get() = ProxyDamageEvent::class.java

    override fun check(e: ProxyDamageEvent): ProxyCommandSender? {
        val player = e.entity as? Player ?: return null
        return adaptPlayer(player)
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
