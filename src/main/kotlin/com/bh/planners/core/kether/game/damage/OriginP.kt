package com.bh.planners.core.kether.game.damage

import ac.github.oa.api.OriginAttributeAPI
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import ac.github.oa.internal.core.attribute.AttributeData
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class OriginP : AttackProvider {

    override fun doDamage(entity: LivingEntity, damage: Double, source: LivingEntity) {
        val damageByEntityEvent = OriginCustomDamageEvent(source, entity, damage, entity, null)

        val attr = OriginAttributeAPI.getAttributeData(source)

        // 创建伤害容器
        val damageMemory = DamageMemory(
            source, entity, damageByEntityEvent, attr, OriginAttributeAPI.getAttributeData(entity)
        )
        damageMemory.addDamage("@Planners",damage)

        if (ac.github.oa.api.event.entity.EntityDamageEvent(damageMemory, PriorityEnum.PRE).call()) {
            OriginAttributeAPI.callDamage(damageMemory)

            // POST CALL
            if (ac.github.oa.api.event.entity.EntityDamageEvent(damageMemory, PriorityEnum.POST).call()) {
                entity.damage(damageMemory.totalDamage.coerceAtLeast(0.0))
            }
        }

    }


}