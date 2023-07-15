package com.bh.planners.api.event.proxy

import ac.github.oa.internal.base.enums.PriorityEnum
import com.bh.planners.core.kether.game.damage.DamageType
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

class ProxyDamagePreEvent(damager: Entity, entity: Entity, cause: EntityDamageEvent.DamageCause?, damage: Double, type: DamageType) :
    AbstractProxyDamageEvent(damager, entity, cause, damage, type) {

    companion object {


        @SubscribeEvent(
            bind = "ac.github.oa.api.event.entity.EntityDamageEvent",
            ignoreCancelled = true,
            priority = EventPriority.LOWEST
        )
        fun e(ope: OptionalEvent) {
            val e = ope.get<ac.github.oa.api.event.entity.EntityDamageEvent>()
            if (e.priorityEnum == PriorityEnum.PRE) {
                val memory = e.damageMemory
                val damager = e.damageMemory.event.damager
                val damageEvent =
                    ProxyDamagePreEvent(damager, memory.injured, memory.event.bukkitCause, memory.totalDamage, DamageType.PHYSICS)
                damageEvent.data["@OriginAttribute:Memory"] = memory
                damageEvent.event = e.damageMemory.event.origin
                damageEvent.call()

                e.isCancelled = damageEvent.isCancelled
                e.damageMemory.event.isCancelled = damageEvent.isCancelled
            }

        }

    }

}