package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersOption
import com.bh.planners.core.pojo.level.LevelOption
import com.bh.planners.core.pojo.level.LevelSystem
import taboolib.library.configuration.ConfigurationSection

class Job(val key: String, val config: ConfigurationSection) {

    val option = Option(config.getConfigurationSection("__option__")!!)
    val skills = config.getStringList("skills")


    class Option(root: ConfigurationSection) {

        val name = root.getString("name")!!
        val counterKey = root.getString("counter")!!

        val counter: LevelOption
            get() = LevelSystem.getLevelOption(counterKey) ?: error("Level counter $counterKey not found")

        val manaCalculate = root.getString("mana-calculate") ?: error("Option 'mana-calculate' not found.")


    }


}