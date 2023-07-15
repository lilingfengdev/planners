package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.AbstractTimer
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import taboolib.module.kether.ScriptContext

object TPlayerDeath : AbstractTimer<ProxyDamageEvent>() {

    override val name: String
        get() = "player death"
    override val eventClazz: Class<ProxyDamageEvent>
        get() = ProxyDamageEvent::class.java

    override fun check(e: ProxyDamageEvent): Target? {
        val player = e.entity as? Player ?: return null
        if (player.health - e.damage > 0) return null
        return player.toTarget()
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
        context.rootFrame().variables()["type"] = e.type
        context.rootFrame().variables()["entity"] = e.entity
        context.rootFrame().variables()["cause"] = e.cause
        context.rootFrame().variables()["damage"] = e.damage
    }

}
