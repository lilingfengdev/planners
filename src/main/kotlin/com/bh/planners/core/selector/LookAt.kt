package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import taboolib.common5.Coerce
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
        var length = data.read<Double>(0, "3")
        val through = data.read<Boolean>(1, "false")
        val direction = location.direction

        direction.multiply(length)

        data.container += location.add(direction).toTarget()

        return CompletableFuture.completedFuture(null)
    }
}