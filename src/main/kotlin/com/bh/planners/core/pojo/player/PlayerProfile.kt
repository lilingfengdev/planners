package com.bh.planners.core.pojo.player

import com.bh.planners.Planners
import com.bh.planners.api.PlannersAPI
import org.bukkit.entity.Player

class PlayerProfile(val player: Player, val id: Long) {

    var job: PlayerJob? = null

    var mana = 0.0

}
