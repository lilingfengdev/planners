package com.bh.planners.core.module.team

import com.bh.planners.api.common.Plugin
import org.bukkit.entity.Player
import org.serverct.ersha.dungeon.DungeonPlus
import org.serverct.ersha.dungeon.common.team.type.PlayerStateType


@Plugin("DungeonPlus")
class TDungeonPlus : Team.Adapter {

    override fun getContainer(player: Player): Team.Container? {
        val team = DungeonPlus.teamManager.getTeam(player) ?: return null
        return object : Team.Container {
            override fun getViewers(): List<Player> {
                return team.getPlayers(PlayerStateType.ALL)
            }

            override fun isViewer(player: Player): Boolean {
                return getViewers().contains(player)
            }

        }
    }



}