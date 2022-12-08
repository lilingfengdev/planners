package com.bh.planners.core.kether.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getEntity
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 选中看向的目的地
 * lookAt length(3),through(false)
 */
object LookAt : Selector {

    override val names: Array<String>
        get() = arrayOf("lookat", "lookAt")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {

        val location = target?.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)
        (target as? Target.Location)
        val split = args.split(",")
        var length = Coerce.toDouble(split.getOrElse(0) { "3" })
        val through = Coerce.toBoolean(split.getOrElse(1) { "false" })
        val direction = location.direction

        direction.multiply(length)

        container += location.add(direction).toTarget()

        return CompletableFuture.completedFuture(null)
    }
}