package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import org.bukkit.entity.LivingEntity
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 选中根据原点来定义的范围实体
 * -@range 10
 * -@range 5 5 5
 */
object Range : Selector {

    override val names: Array<String>
        get() = arrayOf("range", "r")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.target as? Target.Location ?: return CompletableFuture.completedFuture(null)

        val x = data.read<Double>(0,"0.0")
        val y = data.read<Double>(1,"0.0")
        val z = data.read<Double>(2,"0.0")


        val future = CompletableFuture<Void>()
        submit(async = false) {
            location.value.world?.getNearbyEntities(location.value,x,y,z)?.forEach {
                if (it is LivingEntity) {
                    data.container += it.toTarget()
                }
            }
            future.complete(null)
        }
        return future
    }
}
