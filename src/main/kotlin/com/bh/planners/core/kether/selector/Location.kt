package com.bh.planners.core.kether.selector

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import com.bh.planners.core.kether.toLocation
import com.bh.planners.core.pojo.Context
import java.util.concurrent.CompletableFuture

/**
 * 选中具体坐标
 * -@loc world,0,0,0
 * -@location world,0,0,0
 * -@l world,0,0,0
 */
object Location : Selector {
    override val names: Array<String>
        get() = arrayOf("loc", "location", "l")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        container.add(args.toLocation().toTarget())
        return CompletableFuture.completedFuture(null)
    }


}
