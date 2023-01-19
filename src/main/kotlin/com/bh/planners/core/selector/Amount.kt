package com.bh.planners.core.selector

import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 限制选中数量
 * -@amount 1
 * -@size 1
 */
object Amount : Selector {
    override val names: Array<String>
        get() = arrayOf("amount", "size")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val value = data.read<Int>(0,"1")
        if (data.size > value) {
            data.container.remove(data.size - value)
        }
        return CompletableFuture.completedFuture(null)
    }
}
