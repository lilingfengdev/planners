package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.storage.Storage
import org.bukkit.entity.Player

class PlayerProfile(val player: Player, val id: Long) {

    var job: PlayerJob? = null

    val dataContainer = DataContainer()

    val keySlotTable = mutableListOf<PlayerKeySlot>()


    var mana = 0.0

    fun getSkills(): List<PlayerJob.Skill> {
        val skillKeys = job?.instance?.skills ?: emptyList()
        return skillKeys.mapNotNull { getSkill(it) }
    }

    fun getSkill(id: Long): PlayerJob.Skill? {
        if (job == null) return null
        return job!!.skills.firstOrNull { it.id == id }
    }

    fun getSkill(key: String): PlayerJob.Skill? {
        if (job == null) return null
        val playerSkill = job!!.getSkill(key)
        if (playerSkill != null) {
            return playerSkill
        }
        val skill = PlannersAPI.skills.firstOrNull { it.key == key } ?: error("Skill '$key' not found.")
        return Storage.INSTANCE.createPlayerSkill(player, job!!, skill).get().also {
            job!!.skills += it
        }
    }
}
