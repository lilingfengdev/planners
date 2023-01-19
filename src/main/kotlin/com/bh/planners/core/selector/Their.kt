package com.bh.planners.core.selector

import java.util.concurrent.CompletableFuture

/**
 * 选中非释放者的实体 反面意思是过滤自己
 * -@their
 * -@filterthis
 */
object Their : Selector {
    override val names: Array<String>
        get() = arrayOf("their", "filterthis")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val player = data.context.player ?: return CompletableFuture.completedFuture(null)
        data.container.removeIf { it == player }

        return CompletableFuture.completedFuture(null)
    }
}