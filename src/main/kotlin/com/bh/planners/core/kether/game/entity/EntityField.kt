package com.bh.planners.core.kether.game.entity

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

enum class EntityField(
    val get: Entity.() -> Any?
) {

    UUID({ uniqueId }),

    NAME({ name }),

    LOCATION({ location }),

    HEALTH({ (this as? LivingEntity)?.health }),

    MAX_HEALTH({ (this as? LivingEntity)?.maxHealth });


    companion object {

        fun fields() : List<String> {
            return EntityField.values().map { it.name.lowercase() }
        }

    }

}