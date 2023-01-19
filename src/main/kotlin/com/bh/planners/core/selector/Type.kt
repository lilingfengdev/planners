package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target
import java.util.concurrent.CompletableFuture

/**
 * 选中指定实体
 * -@type PLAYER ZOMBIE,...
 * -@t PLAYER ZOMBIE,...
 */
object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t", "!type", "!t")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val types = data.values
        data.container.removeIf {
            if (it is Target.Entity) {
                if (data.isNon) it.type.name in types else it.type.name !in types
            } else true
        }
        return CompletableFuture.completedFuture(null)
    }


}
