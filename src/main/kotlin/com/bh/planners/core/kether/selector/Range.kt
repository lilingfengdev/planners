package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.skill.effect.Target.Companion.toTarget
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 选中根据原点来定义的范围实体
 * -@range 10
 * -@range 5,5,5
 */
object Range : Selector {

    override val names: Array<String>
        get() = arrayOf("range", "r")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val ranges = if (args.contains(",")) args.split(",") else listOf(args, args, args)
        val split = ranges.map { Coerce.toDouble(it) }

        val location = target as? Target.Location ?: return CompletableFuture.completedFuture(null)

        val future = CompletableFuture<Void>()
        submit(async = false) {
            val targets = location.value.world!!
                .getNearbyEntities(location.value, split[0], split[1], split[2])
                .filterIsInstance<LivingEntity>()
                .map { it.toTarget() }
            container.addAll(targets)
            future.complete(null)
        }
        return future
    }
}
