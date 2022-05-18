package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection

class Skill(val config: ConfigurationSection) {

    val key = config.name
    val option = Option(config.getConfigurationSection("__option__")!!)


    class Option(root: ConfigurationSection) {
        val name = root.getString("name")
        val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    class Variable(val key: String, val expression: String)


}
