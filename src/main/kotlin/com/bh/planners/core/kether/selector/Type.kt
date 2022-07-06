package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import java.util.concurrent.CompletableFuture

/**
 * 选中指定实体
 * -@type PLAYER,ZOMBIE,...
 * -@t PLAYER,ZOMBIE,...
 */
object Type : Selector {
    override val names: Array<String>
        get() = arrayOf("type", "t", "!type", "!t")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val types = args.split(",")
        container.removeIf {
            if (this is Target.Entity) {
                if (name.isNon()) type.name in types else type.name !in types
            } else true
        }
        return CompletableFuture.completedFuture(null)
    }


}
