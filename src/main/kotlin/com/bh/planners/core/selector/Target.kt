package com.bh.planners.core.selector

import com.bh.planners.core.kether.ActionTarget.Companion.getTarget
import java.util.concurrent.CompletableFuture

object Target : Selector {
    override val names: Array<String>
        get() = arrayOf("target", "t")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        data.container += (data.context.ketherScriptContext?.rootFrame()?.getTarget() ?: error("Not target."))
        return CompletableFuture.completedFuture(null)
    }
}