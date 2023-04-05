package com.bh.planners.core.selector.bukkit

import com.bh.planners.api.EntityAPI.getDataContainer
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLivingEntity
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * :@flag key=value
 */
object Flag : Selector {
    override val names: Array<String>
        get() = arrayOf("flag", "!flag")

    override fun check(data: Selector.Data): CompletableFuture<Void> {

        data.values.forEach {
            val key = data.read<String>(0, "__null__")
            val value = data.read<String>(1, "__null__")
            if (data.isNon) {
                data.container.removeIf { it.getLivingEntity()?.getDataContainer()?.get(key)?.toString() == value }
            } else {
                data.container.removeIf { it.getLivingEntity()?.getDataContainer()?.get(key)?.toString() != value }
            }

        }




        return CompletableFuture.completedFuture(null)
    }
}