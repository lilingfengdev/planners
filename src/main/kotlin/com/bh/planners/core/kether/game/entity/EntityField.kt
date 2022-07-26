package com.bh.planners.core.kether.game.entity

import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

enum class EntityField(val get: Entity.() -> Any?) {

    UUID({ uniqueId }),

    NAME({ name }),

    TYPE({ type.name }),

    YAW({ location.yaw }),

    PITCH({ location.pitch }),

    HEIGHT({ height }),

    LOCATION({ location }),

    MOVE_SPEED({ (this as? LivingEntity)?.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.value }),

    HEALTH({ (this as? LivingEntity)?.health }),

    VEHICLE({ this.vehicle }),

    MAX_HEALTH({ (this as? LivingEntity)?.maxHealth });


    companion object {

        fun fields(): List<String> {
            return EntityField.values().map { it.name.lowercase() }
        }

    }

}