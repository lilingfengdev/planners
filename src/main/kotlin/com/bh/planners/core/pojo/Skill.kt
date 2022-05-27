package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack

class Skill(val key: String, val config: ConfigurationSection) {

    val option = Option(config.getConfigurationSection("__option__")!!)
    val action = config.getString("action", "")!!


    class Option(val root: ConfigurationSection) {
        val name = root.getString("name")
        val levelCap = root.getInt("level-cap", 5)

        val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    class Variable(val key: String, val expression: String)


}
