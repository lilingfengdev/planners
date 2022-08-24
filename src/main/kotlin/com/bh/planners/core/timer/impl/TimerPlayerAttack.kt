package com.bh.planners.core.timer.impl

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.info
import taboolib.module.kether.ScriptContext
import taboolib.platform.type.BukkitProxyEvent

object TimerPlayerAttack : AbstractTimer<ProxyDamageEvent>() {
    override val name: String
        get() = "player attack"
    override val eventClazz: Class<ProxyDamageEvent>
        get() = ProxyDamageEvent::class.java

    override val priority: EventPriority
        get() = EventPriority.LOWEST

    override fun check(e: ProxyDamageEvent): ProxyCommandSender? {
        return adaptPlayer(e.getPlayer(e.damager) ?: return null)
    }

    /**
     * @Target 被攻击目标
     * damager 攻击者
     * entity 被攻击者
     * projectile? 箭
     * cause 攻击原因
     * damage 攻击伤害
     */
    override fun onStart(context: ScriptContext, template: Template, e: ProxyDamageEvent) {
        super.onStart(context, template, e)

        if (e.entity is LivingEntity) {
            context.rootFrame().variables()["@Target"] = e.entity.toTarget()
        }

        if (e.damager is Projectile) {
            context.rootFrame().variables()["projectile"] = e.damager
        }

        context.rootFrame().variables()["entity"] = e.entity
        context.rootFrame().variables()["cause"] = e.cause
        context.rootFrame().variables()["damage"] = e.damage
    }

}
