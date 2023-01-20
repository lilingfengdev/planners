package com.bh.planners.api.entity

import ink.ptms.adyeshach.common.entity.EntityInstance
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.util.*

class ProxyAdyeshachEntity(val instance: EntityInstance): ProxyEntity {

    val id: String
        get() = instance.id

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

    fun delete() {
        instance.delete()
    }


}