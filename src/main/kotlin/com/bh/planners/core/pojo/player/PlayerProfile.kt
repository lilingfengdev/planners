package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.storage.Storage
import org.bukkit.entity.Player
import taboolib.module.kether.ScriptContext
import java.util.concurrent.ConcurrentHashMap

class PlayerProfile(val player: Player, val id: Long) {

    var job: PlayerJob? = null

    val flags = DataContainer()

    val runningScripts = ConcurrentHashMap<String, ScriptContext>()

    var point: Int = 0
        get() = job?.point ?: 0
        set(value) {
            job?.point = value
            field = value
        }

    val level: Int
        get() = job?.level ?: 0

    val maxExperience: Int
        get() = job?.maxExperience ?: 0

    val experience: Int
        get() = job?.experience ?: 0

    fun getSkills(): List<PlayerJob.Skill> {
        val skillKeys = job?.instance?.skills ?: emptyList()
        return skillKeys.mapNotNull { getSkill(it) }
    }

    fun getSkill(id: Long): PlayerJob.Skill? {
        if (job == null) return null
        return job!!.skills.firstOrNull { it.id == id }
    }

    fun getSkill(slot: IKeySlot): PlayerJob.Skill? {
        if (job == null) return null
        return job!!.skills.firstOrNull { it.keySlot == slot }
    }

    fun getSkill(key: String): PlayerJob.Skill? {
        if (job == null) return null
        val playerSkill = job!!.getSkill(key)
        if (playerSkill != null) {
            return playerSkill
        }
        // 判断技能属不属于职业
        if (job!!.instance.skills.contains(key)) {
            val skill = PlannersAPI.skills.firstOrNull { it.key == key } ?: error("Skill '$key' not found.")
            return Storage.INSTANCE.createPlayerSkill(player, job!!, skill).get().also {
                job!!.skills += it
            }
        }

        return null
    }
}
