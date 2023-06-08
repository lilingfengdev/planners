package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI.plannersProfile
import org.bukkit.entity.Player

class PlayerKeySlot(val key: String, val skillId: Long?) {

    val isBind: Boolean
        get() = skillId != null

    fun getSkill(player: Player): PlayerJob.Skill? {
        return if (isBind) {
            player.plannersProfile.getSkill(skillId!!)
        } else null
    }

}
