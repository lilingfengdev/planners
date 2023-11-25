package com.bh.planners.core.selector.bukkit

import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
import org.bukkit.Material
import java.util.concurrent.CompletableFuture

/**
 * 选中原点下方的第一个方块的位置
 * @under 1
 */
object UnderBlock : Selector {

    override val names: Array<String>
        get() = arrayOf("under")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val location = data.origin.getLocation()?.clone() ?: return CompletableFuture.completedFuture(null)

        val offsetY = data.read<Double>(0, "0")

        while (location.block.type == Material.AIR) {
            location.add(0.0, -1.0, 0.0)
        }

        data.container += location.add(0.0, offsetY, 0.0).toTarget()

        return CompletableFuture.completedFuture(null)
    }

}
