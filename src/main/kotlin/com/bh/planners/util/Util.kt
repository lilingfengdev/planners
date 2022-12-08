package com.bh.planners.util

import com.bh.planners.core.pojo.Skill
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
        amount * (Coerce.toDouble(this.substring(0, this.lastIndex)) / 100)
    } else {
        Coerce.toDouble(this)
    }
}
fun getAction(action: String, func: (Skill.ActionMode) -> Unit): String {
    val first = action.split("\n")[0].trim()

    if (first == "# mode default") {
        func(Skill.ActionMode.DEFAULT)
    } else {
        func(Skill.ActionMode.SIMPLE)
    }

    return action.split("\n").mapNotNull { if (it.trim().getOrNull(0) == '#') null else it }.joinToString("\n")
}

fun Location.clearVisual(): Location {
    this.pitch = 0f
    this.yaw = 0f
    return this
}