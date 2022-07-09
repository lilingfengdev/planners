package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.level.Level
import com.bh.planners.core.storage.Storage

class PlayerJob(val id: Long, var jobKey: String, level: Int, experience: Int) {

    val skills = mutableListOf<Skill>()
    val counter: Level = instance.router.counter.toLevel(level, experience)
    var point: Int = 0

    val instance: Job
        get() = PlannersAPI.getJob(jobKey)

    val level: Int
        get() = counter.level

    val experience: Int
        get() = counter.experience

    val maxExperience: Int
        get() = counter.algorithm.getExp(level).getNow(0)

    val name: String
        get() = instance.option.name

    fun addExperience(value: Int) {
        counter.addExperience(value)
    }

    fun getSkill(skillName: String): Skill? {
        return skills.firstOrNull { it.key == skillName }
    }

    class Skill(val id: Long, val key: String, var level: Int, var shortcutKey: String?) {

        val instance: com.bh.planners.core.pojo.Skill
            get() = PlannersAPI.getSkill(key) ?: error("Skill '$key' not found.")

        val name: String
            get() = instance.option.name

        val keySlot: IKeySlot?
            get() = PlannersAPI.keySlots.firstOrNull { it.key == shortcutKey }

        fun virtual(): Skill {
            return virtual(level)
        }

        fun virtual(level: Int): Skill {
            return Skill(-1, key, level, shortcutKey)
        }


    }

}
