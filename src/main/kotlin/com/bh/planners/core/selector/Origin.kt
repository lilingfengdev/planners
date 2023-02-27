package com.bh.planners.core.selector

import java.util.concurrent.CompletableFuture

object Origin : Selector {
    override val names: Array<String>
        get() = arrayOf("origin", "o")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.container += data.context.origin
        return CompletableFuture.completedFuture(null)
    }
}