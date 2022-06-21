package com.bh.planners.util

import org.bukkit.Location
import org.bukkit.entity.LivingEntity


fun Location.entityAt(): MutableList<LivingEntity> {
    return world!!.getNearbyEntities(this, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>().toMutableList()
}