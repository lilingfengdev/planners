package com.bh.planners.core.pojo.chant

import com.bh.planners.api.common.Id
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Id("default")
class DefaultChantBuilder(duration: Long, period: Long, delay: Long, async: Boolean) : ChantBuilder(duration, period, delay, async) {

    override fun build(sender: Player, tags: List<Interrupt>, context: Session, onTick: (progress: Double) -> Unit): CompletableFuture<Void> {
        return createProgress(Process.Default(sender, context,tags), duration, delay, period, async) {
            onTick(it)
        }
    }

}