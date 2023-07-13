package com.bh.planners.core.module.team

import com.bh.planners.api.common.Plugin
import com.pxpmc.teampro.PxTeamPro
import org.bukkit.entity.Player

@Plugin("PxTeamPro")
class TPxTeam : Team.Adapter {

    override fun getContainer(player: Player): Team.Container? {
        val team = PxTeamPro.inst().api.getTeam(player) ?: return null
        return object : Team.Container {
            override fun getViewers(): List<Player> {
                return team.players.map { it.player }
            }

            override fun isViewer(player: Player): Boolean {
                return team.hasPlayer(player.name)
            }

        }
    }

}