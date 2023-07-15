package com.bh.planners.core.selector.compat

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.module.team.Team
import com.bh.planners.core.selector.Selector
import java.util.concurrent.CompletableFuture

@Plugin("DungeonPlus")
object Team : Selector {

    override val names: Array<String>
        get() = arrayOf("team", "!team")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val entityTarget = data.context.sender

        val player = entityTarget.getPlayer() ?: return CompletableFuture.completedFuture(null)
        val container = Team.INSTANCE?.getContainer(player)
        if (container != null) {
            if (data.name.isNon()) {
                data.container.removeIf { (it is Target.Entity) && container.isViewer(it.player!!) }
            } else {
                data.container.addAll(container.getViewers().map { it.toTarget() })
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}