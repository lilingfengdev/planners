package com.bh.planners.core.timer.bukkit

import com.bh.planners.api.event.proxy.ProxyDamageEvent
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.timer.Template
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptContext
import taboolib.platform.util.attacker

object TPlayerAttack : AbstractTimerDamage() {

    override val name: String
        get() = "player attack"

    override fun check(e: ProxyDamageEvent): Target? {
        return (e.event?.attacker as? Player)?.toTarget()
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

    }

}
