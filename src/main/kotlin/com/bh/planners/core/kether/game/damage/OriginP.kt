package com.bh.planners.core.kether.game.damage

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.ProxyDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import com.bh.planners.api.common.Demand
import com.bh.planners.core.kether.game.ActionDamage
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import taboolib.common5.Coerce

class OriginP : AttackProvider {

    val defaultCause = DamageCause.ENTITY_ATTACK

    override fun doDamage(entity: LivingEntity, damage: Double, source: LivingEntity, demand: Demand) {

        val event = ProxyDamageEvent(EntityDamageByEntityEvent(source, entity, defaultCause, 0.0))
        if (demand.dataMap.containsKey("cause")) {
            event.customCause = demand.get("cause")!!.toString()
        }

        val context = event.createDamageContext()
        // 兼容力度
        context.vigor = damage
        demand.dataMap.forEach {
            context.labels[it.key] = it.value.firstOrNull() ?: return@forEach
        }
        context.addDamage("@Planners", Coerce.toDouble(demand.get("damage")))

        if (ac.github.oa.api.event.entity.EntityDamageEvent(context, PriorityEnum.PRE).call()) {
            OriginAttributeAPI.callDamage(context)
            if (ac.github.oa.api.event.entity.EntityDamageEvent(context, PriorityEnum.POST).call()) {
                ActionDamage.doDamage(source, entity, context.totalDamage.coerceAtLeast(0.0))
            }
        }

    }


}