package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.toLocation
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 选中具体坐标
 * -@loc world 0 0 0
 * -@location world 0 0 0
 * -@l world 0 0 0
 */
object Location : Selector {
    override val names: Array<String>
        get() = arrayOf("loc", "location", "l")

    val Selector.Data.toLocation
        get() = source.toLocation()

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.container += data.toLocation.toTarget()
        return CompletableFuture.completedFuture(null)
    }


}
