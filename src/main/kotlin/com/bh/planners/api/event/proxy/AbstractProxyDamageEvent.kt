package com.bh.planners.api.event.proxy

import ac.github.oa.internal.base.event.impl.DamageMemory
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.platform.type.BukkitProxyEvent
import java.util.*

abstract class AbstractProxyDamageEvent(
    val damager: Entity,
    val entity: Entity,
    val cause: EntityDamageEvent.DamageCause?,
    var damage: Double,
) : BukkitProxyEvent() {

    val data = mutableMapOf<String, Any>()

    var event: EntityDamageByEntityEvent? = null

    val realDamage: Double
        get() = if (isOriginAttribute()) {
            damageMemory()!!.totalDamage
        } else this.damage

    fun addDamage(double: Double) {
        if (isOriginAttribute()) {
            damageMemory()?.addDamage(UUID.randomUUID().toString(), double)
        } else {
            this.damage += double
        }
    }

    companion object {

        fun AbstractProxyDamageEvent.isOriginAttribute(): Boolean {
            return data.containsKey("@OriginAttribute:Memory")
        }

        fun AbstractProxyDamageEvent.damageMemory(): DamageMemory? {
            return data["@OriginAttribute:Memory"] as? DamageMemory
        }

    }

    fun getPlayer(entity: Entity): Player? {
        if (entity is Projectile) {
            return entity.shooter as? Player ?: return null
        }
        return entity as? Player
    }
}