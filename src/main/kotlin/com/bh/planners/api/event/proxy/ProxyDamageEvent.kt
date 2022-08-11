package com.bh.planners.api.event.proxy

import ac.github.oa.api.event.entity.EntityDamageEvent
import ac.github.oa.api.event.entity.OriginCustomDamageEvent
import ac.github.oa.internal.base.enums.PriorityEnum
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

class ProxyDamageEvent(
    val damager: Entity,
    val entity: Entity,
    val cause: DamageCause?,
    var damage: Double
) : BukkitProxyEvent() {

    companion object {

        val isOriginAttribute by lazy { Bukkit.getPluginManager().isPluginEnabled("OriginAttribute") }

        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.MONITOR)
        fun e(e: EntityDamageByEntityEvent) {
            if (isOriginAttribute) return
            val damageEvent = ProxyDamageEvent(e.damager, e.entity, e.cause, e.damage)
            damageEvent.call()
            e.damage = damageEvent.damage
            e.isCancelled = damageEvent.isCancelled
        }

        @SubscribeEvent(
            bind = "ac.github.oa.api.event.entity.EntityDamageEvent",
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
        )
        fun e(ope: OptionalEvent) {
            val e = ope.get<EntityDamageEvent>()
            if (e.priorityEnum == PriorityEnum.POST) {
                val memory = e.damageMemory
                val damageEvent = ProxyDamageEvent(memory.attacker, memory.injured, memory.event.cause, memory.damage)
                damageEvent.call()
                memory.damage = damageEvent.damage
                e.isCancelled = damageEvent.isCancelled
            }

        }

    }

    fun getPlayer(entity: Entity) : Player? {
        if (entity is Projectile) {
            return entity.shooter as? Player ?: return null
        }
        return null
    }



}