package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

object Origin : Selector {
    override val names: Array<String>
        get() = arrayOf("origin", "o")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.container += data.context.origin
        return CompletableFuture.completedFuture(null)
    }
}