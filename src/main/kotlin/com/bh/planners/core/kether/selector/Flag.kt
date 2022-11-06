package com.bh.planners.core.kether.selector

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Context
import java.util.concurrent.CompletableFuture

/**
 * :@flag key=value
 */
object Flag : Selector {
    override val names: Array<String>
        get() = arrayOf("flat","!flag")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val split = args.split("=")
        val key = split[0]
        val value = split[1]
        if (name.isNon()) {
            container.removeIf {
                (it as? Target.Entity)?.entity?.getDataContainer()?.get(key)?.toString() != value
            }
        } else {
            container.removeIf {
                (it as? Target.Entity)?.entity?.getDataContainer()?.get(key)?.toString() == value
            }
        }


        return CompletableFuture.completedFuture(null)
    }
}