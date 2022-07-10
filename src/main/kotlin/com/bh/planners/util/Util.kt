package com.bh.planners.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.common.util.random
import taboolib.common5.Coerce
import kotlin.random.Random

fun Location.entityAt(): MutableList<LivingEntity> {
    return world!!.getNearbyEntities(this, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>().toMutableList()
}

fun generatorId(): Long {
    val millis = System.currentTimeMillis()

    return millis + Random.nextLong(1000000)
}

fun String.eval(amount: Double): Double {
    return if (this.last() == '%') {
        amount * (Coerce.toDouble(this.substring(0, this.lastIndex - 1)) / 100)
    } else {
        Coerce.toDouble(this)
    }
}