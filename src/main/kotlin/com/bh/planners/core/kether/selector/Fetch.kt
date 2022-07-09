package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.catchRunning
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.data.Data
import java.util.concurrent.CompletableFuture

/**
 * 合并目标容器
 * -@fetch t0
 */
object Fetch : Selector {
    override val names: Array<String>
        get() = arrayOf("get", "fetch")

    fun Data.asContainer(): Target.Container {
        return data as Target.Container
    }

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        catchRunning {
            container.merge(context.flags[args]?.asContainer() ?: Target.Container())
        }
        return CompletableFuture.completedFuture(null)
    }
}