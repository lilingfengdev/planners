package com.bh.planners.core.selector

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.core.effect.Target
import java.util.concurrent.CompletableFuture

/**
 * :@flag key=value
 */
object Flag : Selector {
    override val names: Array<String>
        get() = arrayOf("flag","!flag")

    override fun check(data: Selector.Data): CompletableFuture<Void> {

        data.values.forEach {
            val split = it.split("=")
            val key = split[0]
            val value = split[1]
            data.container.removeIf {
                (it as? Target.Entity)?.bukkitEntity?.getDataContainer()?.get(key)?.toString() != value
            }
        }




        return CompletableFuture.completedFuture(null)
    }
}