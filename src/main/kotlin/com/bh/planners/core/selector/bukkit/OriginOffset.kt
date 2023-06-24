package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

object OriginOffset : Selector {

    override val names: Array<String>
        get() = arrayOf("offset", "os")

    override fun check(
        data: Selector.Data,
    ): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)
        val x = data.read<Double>(0, "0.0")
        val y = data.read<Double>(1, "0.0")
        val z = data.read<Double>(2, "0.0")
        data.container += (location.clone().add(x, y, z).toTarget())
        return CompletableFuture.completedFuture(null)
    }
}