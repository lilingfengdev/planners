package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.level.LevelOption
import com.bh.planners.core.pojo.level.LevelSystem
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Configuration

class Router(val config: ConfigurationSection) {

    val key = config.name
    val name = config.getString("name", key)!!
    val icon = config.getItemStack("icon")!!
    val start = config.getString("start")!!
    val counterKey = config.getString("counter")!!
    val regainManaExperience = config.getString("regain-mana-eval")
    val attribute = Skill.Attribute(config.getConfigurationSection("attribute") ?: Configuration.empty())

    val counter: LevelOption
        get() = LevelSystem.getLevelOption(counterKey) ?: error("Level counter $counterKey not found")

    val routes = config.getConfigurationSection("routes")?.getKeys(false)?.map {
        Route(config.getConfigurationSection("routes.$it")!!)
    } ?: emptyList()

    class Route(root: ConfigurationSection) {

        val jobKey = root.name

        val transferJobs = root.getMapList("target").map {
            TransferJob(Configuration.fromMap(it))
        }

        val job: Job
            get() = PlannersAPI.jobs.firstOrNull { it.key == jobKey } ?: error("Job '$jobKey' not found.")

    }

    class TransferJob(val root: ConfigurationSection) {

        val jobKey = root.getString("key")!!
        val extendSkill = root.getBoolean("extend-skill")
        val conditions = root.getMapList("condition").map {
            TransferCondition(Configuration.fromMap(it))
        }

        val job: Job
            get() = PlannersAPI.jobs.firstOrNull { it.key == jobKey } ?: error("Job '$jobKey' not found.")

    }

    class TransferCondition(option: ConfigurationSection) : Condition(option)

}
