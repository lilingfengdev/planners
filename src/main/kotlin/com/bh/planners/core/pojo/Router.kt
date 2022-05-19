package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

class Router(val config: ConfigurationSection) {

    val key = config.name
    val name = config.getString("name", key)!!
    val routes = config.getMapList("routes").map {
        Route(Configuration.fromMap(it))
    }

    class Route(root: ConfigurationSection) {

        val jobKey = root.getString("key")
        val promoteCondition = root.getStringList("promote")
        val extendSkill = root.getBoolean("extend-skill")
        val target = root.getString("target")!!

    }

}
