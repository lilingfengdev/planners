package com.bh.planners.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import taboolib.common.util.random
import kotlin.random.Random

fun Location.entityAt(): MutableList<LivingEntity> {
    return world!!.getNearbyEntities(this, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>().toMutableList()
}

fun generatorId(): Long {
    val millis = System.currentTimeMillis()

    return millis + Random.nextLong(1000000)
}