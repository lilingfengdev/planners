package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

/**
 * 选中自己
 * @self
 * @this
 * 选中自己脚下坐标
 * @self m keepVisual(false)
 */
object Self : Selector {

    override val names: Array<String>
        get() = arrayOf("self", "this", "!self", "!this")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val sender = data.context.sender
        val keepVisual = data.read<Boolean>(1, "false")
        // 反选
        if (data.isNon) {
            data.container.removeIf { it == sender }
        }
        // 选中脚下
        else if (data.read<String>(0, "__null__") == "m") {
            val location = sender.getLocation() ?: return CompletableFuture.completedFuture(null)
            if (!keepVisual) {
                location.pitch = 0f
                location.yaw = 0f
            }
            location.y -= 1.5
            data.container += Target.Location(location)
        }
        // 默认选中
        else {
            data.container += sender
        }
        return CompletableFuture.completedFuture(null)
    }

}
