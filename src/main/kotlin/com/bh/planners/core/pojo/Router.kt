package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection

class Router(val config: ConfigurationSection) {

    val key = config.name
    val name = config.getString("name", key)!!
    val routes = config.getConfigurationSection("router")?.getKeys(false)?.map {
        Router(config.getConfigurationSection(it)!!)
    }


    class Route(root: ConfigurationSection) {

        val promoteCondition = root.getStringList("promote")


    }

}
