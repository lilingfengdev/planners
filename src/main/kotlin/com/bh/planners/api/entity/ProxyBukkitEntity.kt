package com.bh.planners.api.entity

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import java.util.*

class ProxyBukkitEntity(val instance: Entity) : ProxyEntity {

    override val location: Location
        get() = instance.location

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


}