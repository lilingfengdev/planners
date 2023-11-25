package com.bh.planners.core.selector.bukkit.condition

import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 限制选中数量
 * @amount 1
 * @size 1
 */
object Amount : Selector {
    override val names: Array<String>
        get() = arrayOf("amount", "size")

    override fun check(data: Selector.Data): CompletableFuture<Void> {

        val value = data.read<Int>(0, "1")
        val first = data.read<Boolean>(1, "true")

        if (data.size > value) {
            data.container.remove(data.size - value, first)
        }

        return CompletableFuture.completedFuture(null)
    }
}
