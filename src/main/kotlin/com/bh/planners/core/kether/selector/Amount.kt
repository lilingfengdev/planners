package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
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

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val value = Coerce.toInteger(args)
        if (container.size > value) {
            container.remove(container.size - value)
        }
        return CompletableFuture.completedFuture(null)
    }
}
