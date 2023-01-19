package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

object OriginOffset : Selector {

    override val names: Array<String>
        get() = arrayOf("offset")

    override fun check(
        data: Selector.Data
    ): CompletableFuture<Void> {
        val location = data.target as? Target.Location ?: return CompletableFuture.completedFuture(null)
        val x = data.read<Double>(0,"0.0")
        val y = data.read<Double>(1,"0.0")
        val z = data.read<Double>(2,"0.0")
        data.container += (location.value.clone().add(x, y, z).toTarget())
        return CompletableFuture.completedFuture(null)
    }
}