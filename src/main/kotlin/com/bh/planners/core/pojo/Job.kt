package com.bh.planners.core.pojo

import com.bh.planners.Planners
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersOption
import com.bh.planners.core.pojo.level.LevelOption
import com.bh.planners.core.pojo.level.LevelSystem
import taboolib.library.configuration.ConfigurationSection

class Job(val key: String, val config: ConfigurationSection) {

    val option = Option(config.getConfigurationSection("__option__")!!)
    val skills = config.getStringList("skills")

    val router: Router
        get() = PlannersAPI.routers.firstOrNull { it.key == option.routerKey }
            ?: error("Router ${option.routerKey} not found")

    class Option(root: ConfigurationSection) {

        val name = root.getString("name")!!
        val routerKey = root.getString("router")!!
        val actionbar = root.getString("actionbar")
        val regainManaExperience = root.getString("regain-mana-eval")

        val router : Router
            get() = PlannersAPI.getRouter(routerKey)

        val manaCalculate = root.getString("mana-eval") ?: error("Option 'mana-eval' not found.")


    }


}
