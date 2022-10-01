package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import taboolib.common.platform.function.info
import taboolib.platform.type.BukkitPlayer
import java.util.concurrent.CompletableFuture

/**
 * 选中自己
 * -@self
 * -@this
 * 选中自己脚下坐标
 * -@self m
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val executor = context.executor as? BukkitPlayer ?: return CompletableFuture.completedFuture(null)
        val entity = executor.player.toTarget()
        if (name.isNon()) {
            container.removeIf { it == entity }
        } else {
            container.add(if (args.contains("m")) Target.Location(entity.entity.location) else entity)
        }
        return CompletableFuture.completedFuture(null)
    }

}
