package com.bh.planners.api.entity

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.util.Vector
import java.util.*

class ProxyBukkitEntity(val instance: Entity) : ProxyEntity {

    override val isDead: Boolean
        get() = instance.isDead

    override val location: Location
        get() = instance.location

    override val eyeLocation: Location
        get() = (instance as? LivingEntity)?.eyeLocation ?: location

    override val uniqueId: UUID
        get() = instance.uniqueId

    override val world: World
        get() = instance.world

    override val name: String
        get() = instance.name

    override var customName: String?
        get() = instance.customName
        set(value) {
            instance.customName = value
        }

    override val type: String
        get() = instance.type.name

    override val bukkitType: EntityType
        get() = instance.type

    override val height: Double
        get() = instance.height

    override val vehicle: ProxyEntity?
        get() = instance.vehicle?.run { ProxyBukkitEntity(this) }

    val isLivingEntity = instance is LivingEntity

    override var velocity: Vector
        get() = instance.velocity
        set(value) {
            instance.velocity = value
        }

    override fun hashCode(): Int {
        return instance.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is ProxyBukkitEntity) {
            return instance == other.instance
        }

        if (other is Entity) {
            return instance == other
        }

        return false
    }

    override fun toString(): String {
        return instance.name
    }


}