package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.effect.common.PlayerFrontCoordinate
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture
import kotlin.math.cos
import kotlin.math.sin

/**
 * -@c-dot radius y angle keepVisual(false)
 * -@c-dot 2
 */
object CircleDot : Selector {

    override val names: Array<String>
        get() = arrayOf("c-dot", "cdot", "cd")

    override fun check(
        data: Selector.Data
    ): CompletableFuture<Void> {
        val location = data.origin.getLocation() ?: return CompletableFuture.completedFuture(null)
        val coordinate = PlayerFrontCoordinate(location)
        val radius = data.read<Double>(0,"1")
        val y = data.read<Double>(1,"0.0")
        val angle = data.read<Double>(2,"0.0")
        val isKeepVisual = data.read<Boolean>(3,"true")
        val radians = Math.toRadians(angle)
        val x: Double = radius * cos(radians)
        val z: Double = radius * sin(radians)
        val newLocation = coordinate.newLocation(x, y, z)
        if (isKeepVisual) {
            newLocation.pitch = location.pitch
            newLocation.yaw = location.yaw
        }
        data.container += (newLocation.toTarget())
        return CompletableFuture.completedFuture(null)
    }
}