package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Configuration

class Router(val config: ConfigurationSection) {

    val key = config.name
    val name = config.getString("name", key)!!
    val icon = config.getItemStack("icon")!!
    val routes = config.getMapList("routes").map {
        Route(Configuration.fromMap(it))
    }

    class Route(root: ConfigurationSection) {

        val jobKey = root.getString("key")!!
        val promoteCondition = root.getStringList("promote")
        val extendSkill = root.getBoolean("extend-skill")
        val target = root.getString("target")!!

        val job: Job
            get() = PlannersAPI.jobs.firstOrNull { it.key == jobKey } ?: error("Job '$jobKey' not found.")

    }

}
