package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.level.Level
import com.bh.planners.core.storage.Storage

class PlayerJob(val id: Long, val jobKey: String, level: Int, experience: Int) {

    val skills = mutableListOf<Skill>()
    val counter: Level = instance.option.counter.toLevel(level, experience)

    val instance: Job
        get() = PlannersAPI.jobs.first { it.key == jobKey }

    fun getSkill(skillName: String): Skill? {
        return skills.firstOrNull { it.key == skillName }
    }

    class Skill(val id: Long, val key: String, var level: Int) {

        val instance: com.bh.planners.core.pojo.Skill
            get() = PlannersAPI.skills.first { it.key == key }

    }

}
