package com.bh.planners.core.selector

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.toTarget
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.serverct.ersha.dungeon.DungeonPlus
import org.serverct.ersha.dungeon.common.team.type.PlayerStateType
import java.util.concurrent.CompletableFuture

@Plugin("DungeonPlus")
object Team : Selector {
    override val names: Array<String>
        get() = arrayOf("team", "!team")

    override fun check(data: Selector.Data): CompletableFuture<Void> {
        val entityTarget = data.target as? Target.Entity ?: return CompletableFuture.completedFuture(null)

        val player = (entityTarget.entity as? Player) ?: return CompletableFuture.completedFuture(null)

        val team = getTeamInstance(player) ?: return CompletableFuture.completedFuture(null)

        if (data.name.isNon()) {
            data.container.removeIf { it is Target.Entity && it.entity in team.players }
        } else {
            data.container.addAll(team.players.map { it.toTarget() })
        }

        return CompletableFuture.completedFuture(null)
    }

    fun getTeamInstance(player: Player): Instance? {

        if (Bukkit.getPluginManager().isPluginEnabled("DungeonPlus")) {
            return DungeonPlusTeam(player)
        }
        return null
    }

    class DungeonPlusTeam(val player: Player) : Instance {

        val team = DungeonPlus.teamManager.getTeam(player)

        override val isValid = team != null

        override val players: List<Player>
            get() = team?.getPlayers(PlayerStateType.ALL) ?: emptyList()

    }

    interface Instance {

        val players: List<Player>

        val isValid: Boolean

    }

}