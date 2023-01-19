package com.bh.planners.core.selector

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import taboolib.common5.Coerce
import taboolib.platform.type.BukkitPlayer
import java.util.concurrent.CompletableFuture

/**
 * 选中自己
 * -@self
 * -@this
 * 选中自己脚下坐标
 * -@self m keepVisual(false)
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val executor = data.context.proxySender as? BukkitPlayer ?: return CompletableFuture.completedFuture(null)
        val keepVisual = data.read<Boolean>(1,"false")
        val entity = executor.player.toTarget()
        if (data.isNon) {
            data.container.removeIf { it == entity }
        } else {

            if (data.read<String>(0,"__null__") == "m") {
                val location = entity.entity.location
                if (!keepVisual) {
                    location.pitch = 0f
                    location.yaw = 0f
                }
                data.container += Target.Location(location)
            } else {
                data.container += executor.player.toTarget()
            }

        }
        return CompletableFuture.completedFuture(null)
    }

}
