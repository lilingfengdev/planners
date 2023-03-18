package com.bh.planners.api.event.proxy

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import ac.github.oa.internal.base.event.impl.DamageMemory
import com.bh.planners.api.event.EntityEvents
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.type.BukkitProxyEvent
import java.util.UUID

open class ProxyDamageEvent(damager: Entity, entity: Entity, cause: DamageCause?, damage: Double) : AbstractProxyDamageEvent(damager, entity, cause, damage) {

    companion object {

        val isOriginAttribute by lazy { Bukkit.getPluginManager().isPluginEnabled("OriginAttribute") }

        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun e(e: EntityDamageByEntityEvent) {
            if (isOriginAttribute) return
            val damageEvent = ProxyDamageEvent(e.damager, e.entity, e.cause, e.damage)
            damageEvent.event = e
            damageEvent.call()
            e.damage = damageEvent.damage
            if (damageEvent.isCancelled) {
                e.isCancelled = true
            }
        }


        @SubscribeEvent(
            bind = "ac.github.oa.api.event.entity.EntityDamageEvent",
            ignoreCancelled = true,
            priority = EventPriority.LOWEST
        )
        fun e(ope: OptionalEvent) {
            val e = ope.get<EntityDamageEvent>()
            if (e.priorityEnum == PriorityEnum.POST) {
                val memory = e.damageMemory
                val damager = e.damageMemory.event.damager
                val damageEvent =
                    ProxyDamageEvent(damager, memory.injured, memory.event.bukkitCause, memory.totalDamage)
                damageEvent.data["@OriginAttribute:Memory"] = memory
                damageEvent.event = e.damageMemory.event.origin
                damageEvent.call()

                e.isCancelled = damageEvent.isCancelled
                e.damageMemory.event.isCancelled = damageEvent.isCancelled
            }

        }

        @SubscribeEvent
        fun e(e: EntityEvents.DamageByEntity) {
            if (e.damager != null) {
                val damageEvent = ProxyDamageEvent(e.damager, e.entity, DamageCause.CUSTOM, e.value)
                e.value = damageEvent.realDamage
                if (!damageEvent.call()) {
                    damageEvent.isCancelled = true
                }
            }

        }

    }


}