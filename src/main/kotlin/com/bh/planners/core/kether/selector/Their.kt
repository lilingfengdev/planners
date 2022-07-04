package com.bh.planners.core.kether.selector

import com.bh.planners.core.kether.asPlayer
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import taboolib.common.platform.function.info
import java.util.concurrent.CompletableFuture

/**
 * 选中非释放者的实体 反面意思是过滤自己
 * -@their
 * -@filterthis
 */
object Their : Selector {
    override val names: Array<String>
        get() = arrayOf("their", "filterthis")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val player = context.executor.asPlayer() ?: return CompletableFuture.completedFuture(null)
        container.removeIf { this == player }

        return CompletableFuture.completedFuture(null)
    }
}