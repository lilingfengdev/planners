package com.bh.planners.core.kether.selector

import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.platform.type.BukkitPlayer
import java.util.concurrent.CompletableFuture

/**
 * 选中自己
 * -@self
 * -@this
 * 选中自己脚下坐标
 * -@self m,keepVisual(false)
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val executor = context.executor as? BukkitPlayer ?: return CompletableFuture.completedFuture(null)
        val split = args.split(",")
        val keepVisual = Coerce.toBoolean(split.getOrElse(1) { "false" })
        val entity = executor.player.toTarget()
        if (name.isNon()) {
            container.removeIf { it == entity }
        } else {

            if (split[0] == "m") {
                val location = entity.entity.location
                if (!keepVisual) {
                    location.pitch = 0f
                    location.yaw = 0f
                }
                container += Target.Location(location)
            } else {
                container += executor.player.toTarget()
            }

        }
        return CompletableFuture.completedFuture(null)
    }

}
