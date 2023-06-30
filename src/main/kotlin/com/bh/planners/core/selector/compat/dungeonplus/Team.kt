package com.bh.planners.core.selector.compat.dungeonplus

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.effect.Target
import com.bh.planners.core.effect.Target.Companion.getPlayer
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.selector.Selector
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
        val entityTarget = data.context.sender

        val player = entityTarget.getPlayer() ?: return CompletableFuture.completedFuture(null)

        val team = getTeamInstance(player) ?: return CompletableFuture.completedFuture(null)

        if (data.name.isNon()) {
            data.container.removeIf { (it is Target.Entity) && team.players.contains(it.player) }
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
            get() = team?.getPlayers(PlayerStateType.ONLINE) ?: emptyList()

    }

    interface Instance {

        val players: List<Player>

        val isValid: Boolean

    }

}