package com.bh.planners.core.selector.bukkit

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.core.effect.Target
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

object Job : Selector {
    override val names: Array<String>
        get() = arrayOf("job", "!job")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val name = data.read<String>(0, "__null__")
        data.container.removeIf {
            (it as? Target.Entity)?.player?.plannersProfile?.job?.jobKey == name
        }
        return CompletableFuture.completedFuture(null)
    }
}