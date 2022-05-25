package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection

class Skill(val key : String,val config: ConfigurationSection) {

    val option = Option(config.getConfigurationSection("__option__")!!)
    val action = config.getString("action", "")!!


    class Option(root: ConfigurationSection) {
        val name = root.getString("name")
        val mpCost = root.getString("mp-cost", "1")!!

        val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    class Variable(val key: String, val expression: String)


}
