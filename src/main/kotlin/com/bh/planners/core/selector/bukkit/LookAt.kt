package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 选中看向的目的地
 * lookAt length(3) through(false)
 */
object LookAt : Selector {

    override val names: Array<String>
        get() = arrayOf("lookat", "lookAt")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)
        val length = data.read<Double>(0, "3")
        val direction = location.direction

        direction.multiply(length)

        data.container += location.add(direction).toTarget()

        return CompletableFuture.completedFuture(null)
    }
}