package com.bh.planners.api.entity

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import java.util.*

class ProxyAdyeshachEntity(val instance: EntityInstance) : ProxyEntity {

    companion object {


        fun ProxyEntity.getAdyeshachEntity(): EntityInstance? {
            return (this as? ProxyAdyeshachEntity)?.instance
        }

    }

    override val entityId: Int
        get() = instance.index

    val id: String
        get() = instance.id

    override val isDead: Boolean
        get() = instance.isDeleted

    override val uniqueId: UUID
        get() = instance.normalizeUniqueId

    override val world: World
        get() = instance.getWorld()

    override val name: String
        get() = instance.getDisplayName()

    val isDeleted: Boolean
        get() = instance.isDeleted

    override var customName: String?
        get() = instance.getCustomName()
        set(value) {
            instance.setCustomName(value ?: error("Not name."))
        }

    override val type: String
        get() = instance.entityType.name

    override val bukkitType: EntityType?
        get() = instance.entityType.bukkitType

    override val height: Double
        get() = instance.entityType.entitySize.height

    override val vehicle: ProxyEntity?
        get() = instance.getVehicle()?.run { ProxyAdyeshachEntity(this) }

    override val location: Location
        get() = instance.getLocation()

    override val eyeLocation: Location
        get() = location

    override fun delete() {
        instance.delete()
    }

    override var velocity: Vector
        get() = Vector(0, 0, 0)
        set(value) {
            val vector = ink.ptms.adyeshach.taboolib.common.util.Vector(value.x, value.y, value.z)
            instance.sendVelocity(vector)
        }

    override fun hashCode(): Int {
        return instance.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other is EntityInstance) {
            return instance == other
        }

        if (other is ProxyAdyeshachEntity) {
            return other.instance == instance
        }

        return false
    }

    override fun teleport(location: Location) {
        instance.teleport(location)
    }

    override fun toString(): String {
        return instance.getCustomName()
    }


}