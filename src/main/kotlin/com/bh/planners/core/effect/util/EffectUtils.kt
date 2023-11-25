package com.bh.planners.core.effect.util

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.effect.EffectOption
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.isPrimaryThread
import taboolib.common.platform.function.submit
import taboolib.common.util.sync
import java.util.concurrent.CompletableFuture

fun Map<String, EffectOption.Option>.get(titles: Array<String>, def: String? = null): EffectOption.Option? {
    return filter { it.key in titles }.values.firstOrNull()
        ?: if (def == null) {
            null
        } else {
            EffectOption.Option(def)
        }
}

fun Map<String, EffectOption.Option>.get(title: String, def: String? = null): EffectOption.Option? {
    return get(arrayOf(title), def)
}

private fun Location.getNearbyEntities(): List<LivingEntity> {
    return world!!.getNearbyEntities(
        this,
        PlannersOption.scopeThreshold.get()[0],
        PlannersOption.scopeThreshold.get()[1],
        PlannersOption.scopeThreshold.get()[2]
    ).filterIsInstance<LivingEntity>()
}

fun Location.getNearbyEntities(radius: Double): List<LivingEntity> {
    return world!!.getNearbyEntities(
        this,
        radius,
        radius,
        radius
    ).filterIsInstance<LivingEntity>()
}

fun createAwaitVoidFuture(block: () -> Unit): CompletableFuture<Void> {
    return if (isPrimaryThread) {
        block()
        CompletableFuture.completedFuture(null)
    } else {
        sync { block() }
        CompletableFuture.completedFuture(null)
    }
}

fun <T> createAwaitFuture(block: () -> T): CompletableFuture<T> {
    if (isPrimaryThread) {
        error("Cannot run sync task in main thread.")
    }
    val future = CompletableFuture<T>()
    submit { future.complete(block()) }
    return future
}

fun Location.capture(): CompletableFuture<List<LivingEntity>> {
    return createAwaitFuture { this.getNearbyEntities() }
}