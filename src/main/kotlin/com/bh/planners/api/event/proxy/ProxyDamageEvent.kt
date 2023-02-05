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

class ProxyDamageEvent(val damager: Entity, val entity: Entity, val cause: DamageCause?, var damage: Double, var memory: Any? = null) : BukkitProxyEvent() {

    var event : EntityDamageByEntityEvent? = null

    val realDamage : Double
        get() = (memory as? DamageMemory)?.totalDamage ?: this.damage

    fun addDamage(double: Double) {
        if (memory != null) {
            (memory as? DamageMemory)?.addDamage(UUID.randomUUID().toString(), double)
        } else {
            this.damage += double
        }
    }

    companion object {

        val isOriginAttribute by lazy { Bukkit.getPluginManager().isPluginEnabled("OriginAttribute") }

        @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.LOWEST)
        fun e(e: EntityDamageByEntityEvent) {
            if (isOriginAttribute) return
            val damageEvent = ProxyDamageEvent(e.damager, e.entity, e.cause, e.damage)
            damageEvent.event = e
            damageEvent.call()
            e.damage = damageEvent.damage
            e.isCancelled = damageEvent.isCancelled
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
                val damageEvent = ProxyDamageEvent(damager, memory.injured, memory.event.cause, memory.totalDamage, memory)
                damageEvent.event = e.damageMemory.event.origin
                damageEvent.call()

                e.isCancelled = damageEvent.isCancelled
                e.damageMemory.event.isCancelled = damageEvent.isCancelled
            }

        }

        @SubscribeEvent
        fun e(e: EntityEvents.DamageByEntity) {
            if (e.damager != null) {
                val damageEvent = ProxyDamageEvent(e.damager, e.entity, DamageCause.CUSTOM, e.value, null)
                e.value = damageEvent.realDamage
                if (!damageEvent.call()) {
                    damageEvent.isCancelled = true
                }
            }

        }

    }

    fun getPlayer(entity: Entity): Player? {
        if (entity is Projectile) {
            return entity.shooter as? Player ?: return null
        }
        return entity as? Player
    }


}