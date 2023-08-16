package com.bh.planners.util

import com.bh.planners.api.ContextAPI
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.ScriptFactor
import com.bh.planners.core.pojo.Skill
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.common.util.sync
import taboolib.common5.Coerce
import taboolib.module.kether.printKetherErrorMessage
import java.util.*
import kotlin.random.Random

fun Target.toProxyCommandSender(): ProxyCommandSender? {
    return ContextAPI.createProxy(this.getPlayer() ?: return null)
}

fun Location.entityAt(): MutableList<LivingEntity> {
    return world!!.getNearbyEntities(this, 1.0, 1.0, 1.0).filterIsInstance<LivingEntity>().toMutableList()
}


fun <T> runKetherThrow(context: Context, el: T? = null, function: () -> T?): T? {
    return runKetherThrow(context.stackId, el, function)
}

fun <T> runKetherThrow(stack: String?, el: T? = null, function: () -> T): T? {
    try {
        return function()
    } catch (ex: Exception) {
        if (stack != null) {
            warning("Track the error by the '$stack' source")
        }
        ex.printKetherErrorMessage()
    }
    return el
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

fun getScriptFactor(action: String): ScriptFactor {

    val first = action.split("\n")[0].trim()
    return if (first == "# mode default" || first == "def main = {") {
        ScriptFactor(
            Skill.ActionMode.DEFAULT,
            action.split("\n").mapNotNull { if (it.trim().getOrNull(0) == '#') null else it }.joinToString("\n")
        )
    } else {
        ScriptFactor(
            Skill.ActionMode.SIMPLE,
            action.split("\n").mapNotNull { if (it.trim().getOrNull(0) == '#') null else it }.joinToString("\n")
        )
    }
}

fun Location.clearVisual(): Location {
    this.pitch = 0f
    this.yaw = 0f
    return this
}

fun timing(): Long {
    return System.currentTimeMillis()
}

fun timing(start: Long): Double {
    return Coerce.format((System.currentTimeMillis() - start) / 100.0, 2)
}

fun List<String>.upperCase(): List<String> {
    return map { it.uppercase(Locale.getDefault()) }
}

fun safeAsync(job: () -> Unit) {
    if (Bukkit.isPrimaryThread()) {
        submitAsync {
            job()
        }
    } else {
        job()
    }
}

fun safeSync(job: () -> Unit) {
    if (Bukkit.isPrimaryThread()) {
        job()
    } else {
        sync { job() }
    }
}