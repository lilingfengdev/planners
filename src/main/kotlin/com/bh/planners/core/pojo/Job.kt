package com.bh.planners.core.pojo

import com.bh.planners.api.PlannersAPI
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

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
        val attribute = Skill.Attribute(root.getConfigurationSection("attribute") ?: Configuration.empty())

        val router: Router
            get() = PlannersAPI.getRouter(routerKey)

        val manaCalculate = root.getString("mana-eval") ?: error("Option 'mana-eval' not found.")


    }


}
