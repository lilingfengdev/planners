package com.bh.planners.core.kether.selector

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import org.bukkit.entity.Player
import org.serverct.ersha.dungeon.DungeonPlus
import java.util.concurrent.CompletableFuture

@Plugin("DungeonPlus")
object Team : Selector {
    override val names: Array<String>
        get() = arrayOf("team", "!team")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container): CompletableFuture<Void> {
        val entityTarget = target as? Target.Entity ?: return CompletableFuture.completedFuture(null)
        if (entityTarget.entity !is Player) return CompletableFuture.completedFuture(null)

        val team = DungeonPlus.teamManager.getTeam(entityTarget.entity) ?: return CompletableFuture.completedFuture(null)
        team.getOfflinePlayers().forEach {  }

        if (name.isNon()) {

        } else {

        }
        return CompletableFuture.completedFuture(null)
    }
}