package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
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
        val ranges = args.split(",");
        val x = Coerce.toDouble(ranges[0])
        val y = Coerce.toDouble(ranges.getOrElse(1) { ranges[0] })
        val z = Coerce.toDouble(ranges.getOrElse(2) { ranges[0] })

        val location = target as? Target.Location ?: return CompletableFuture.completedFuture(null)

        val future = CompletableFuture<Void>()
        submit(async = false) {
            container += location.value.world?.getNearbyEntities(location.value, x,y,z)?.filterIsInstance<LivingEntity>()
                ?.map { it.toTarget() } ?: emptyList()
            future.complete(null)
        }
        return future
    }
}
