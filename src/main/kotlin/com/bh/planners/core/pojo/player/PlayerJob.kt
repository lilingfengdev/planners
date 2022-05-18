package com.bh.planners.core.pojo.player

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.level.Level

class PlayerJob(val jobKey: String, level: Int, experience: Int) {

    val skills = mutableListOf<Skill>()
    val counter: Level = instance.option.counter.toLevel(level, experience)

    private val instance: Job
        get() = PlannersAPI.jobs.first { it.key == jobKey }

    fun getSkill(key: String): Skill {
        return skills.firstOrNull { it.key == key } ?: Skill(key, 0)
    }

    class Skill(val key: String, var level: Int) {

        val instance: com.bh.planners.core.pojo.Skill
            get() = PlannersAPI.skills.first { it.key == key }

    }

}
