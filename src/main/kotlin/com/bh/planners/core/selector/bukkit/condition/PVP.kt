package com.bh.planners.core.selector.bukkit.condition

import com.bh.planners.core.effect.Target
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

// 过滤器 过滤pvp
object PVP : Selector {

    override val names: Array<String>
        get() = arrayOf("pvp", "!pvp")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        if (data.name.isNon()) {
            data.container.removeIf { (it is Target.Entity) && it.player?.world?.pvp == true }
        } else {
            data.container.removeIf { (it is Target.Entity) && it.player?.world?.pvp == false }
        }
        return CompletableFuture.completedFuture(null)
    }


}