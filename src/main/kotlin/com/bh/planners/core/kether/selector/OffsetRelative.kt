package com.bh.planners.core.kether.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import taboolib.common5.Coerce
import java.util.concurrent.CompletableFuture

/**
 * 该操作会把实体目标转换为坐标目标
 * :@offset-r 1,1,1,0,0 基于原有地址偏移(1,1,1)xyz,pitch,yaw
 */
object OffsetRelative : Selector {

    override val names: Array<String>
        get() = arrayOf("offset-r", "offsetr", "offset-relative")

    override fun check(
        name: String,
        target: Target?,
        args: String,
        context: Context,
        container: Target.Container
    ): CompletableFuture<Void> {
        val split = args.split(",").map { Coerce.toDouble(it) }
        val x = split.getOrElse(0) { 0.0 }
        val y = split.getOrElse(1) { 0.0 }
        val z = split.getOrElse(2) { 0.0 }
        val pitch = Coerce.toFloat(split.getOrElse(3) { 0.0 })
        val yaw = Coerce.toFloat(split.getOrElse(4) { 0.0 })

        val removes = mutableListOf<Target>()
        val addons = mutableListOf<Target>()

        container.forEach {
            if (it is Target.Entity) {
                removes += it

                val location = it.value.clone()
                location.add(x, y, z)
                location.yaw += yaw
                location.pitch += pitch
                addons += location.toTarget()

            } else if (it is Target.Location) {
                it.value.add(split[0], split[1], split[2])
            }
        }

        container.removeIf { it in removes }
        container.addAll(addons)

        return CompletableFuture.completedFuture(null)
    }
}