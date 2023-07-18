package com.bh.planners.api.event.proxy

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
import com.bh.planners.api.event.EntityEvents
import com.bh.planners.core.kether.game.damage.DamageType
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityDamageEvent.DamageCause.*
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent

open class ProxyDamageEvent(damager: Entity, entity: Entity, cause: DamageCause?, damage: Double,type: DamageType) : AbstractProxyDamageEvent(damager, entity, cause, damage, type) {

    companion object {

        private val isOriginAttribute by lazy { Bukkit.getPluginManager().isPluginEnabled("OriginAttribute") }

        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun e(e: EntityDamageByEntityEvent) {
            if (isOriginAttribute) return
            val damageEvent = ProxyDamageEvent(e.damager, e.entity, e.cause, e.damage, when(e.cause) {
                CUSTOM -> DamageType.MAGIC
                FALL -> DamageType.FALL
                ENTITY_ATTACK -> DamageType.PHYSICS
                DRAGON_BREATH -> DamageType.MAGIC
                MAGIC -> DamageType.MAGIC
                ENTITY_EXPLOSION -> DamageType.MAGIC
                BLOCK_EXPLOSION -> DamageType.MAGIC
                PROJECTILE -> DamageType.PHYSICS
                FIRE -> DamageType.MAGIC
                DRYOUT -> DamageType.CHEMISTRY
                DROWNING -> DamageType.CHEMISTRY
                SUICIDE -> DamageType.CONSOLE
                LAVA -> DamageType.CHEMISTRY
                POISON -> DamageType.CHEMISTRY
                VOID -> DamageType.CONSOLE
                LIGHTNING -> DamageType.MAGIC
                HOT_FLOOR -> DamageType.CHEMISTRY
                FREEZE -> DamageType.CHEMISTRY
                CRAMMING -> DamageType.CRAMMING
                THORNS -> DamageType.BLOCK
                CONTACT -> DamageType.BLOCK
                ENTITY_SWEEP_ATTACK -> DamageType.PHYSICS
                SUFFOCATION -> DamageType.CHEMISTRY
                FIRE_TICK -> DamageType.MAGIC
                MELTING -> DamageType.BUKKIT
                STARVATION -> DamageType.CHEMISTRY
                WITHER -> DamageType.MAGIC
                FALLING_BLOCK -> DamageType.PHYSICS
                FLY_INTO_WALL -> DamageType.PHYSICS
            })
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
        fun oa(ope: OptionalEvent) {
            val e = ope.get<EntityDamageEvent>()
            if (e.priorityEnum == PriorityEnum.POST) {
                val memory = e.damageMemory
                val damager = e.damageMemory.event.damager
                val damageEvent =
                    ProxyDamageEvent(damager, memory.injured, memory.event.bukkitCause, memory.totalDamage, if (e.damageMemory.cause == "magic") DamageType.MAGIC else DamageType.PHYSICS)
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
                val damageEvent = ProxyDamageEvent(e.damager, e.entity, CUSTOM, e.value, e.damageType)
                damageEvent.call()
                e.value = damageEvent.realDamage

                if (damageEvent.isCancelled) {
                    e.isCancelled = true
                }
            }
        }

    }


}