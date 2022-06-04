package com.bh.planners.core.pojo

import com.bh.planners.core.timer.Template
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Configuration

open class Skill(val key: String, val config: ConfigurationSection) {

    open val option = Option(config.getConfigurationSection("__option__") ?: config.createSection("__option__"))
    open val action = config.getString("action", "")!!

    open val timers = config.getConfigurationSection("timer")?.getKeys(false)?.map {
        InlineTimer(it, config.getConfigurationSection("timer.$it")!!)
    }

    open class Option(val root: ConfigurationSection) {
        open val name = root.getString("name")!!
        open val levelCap = root.getInt("level-cap", 5)
        open val async = root.getBoolean("async", false)

        open val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    class Variable(val key: String, val expression: String)

    class InlineTimer(id: String, root: ConfigurationSection) : Template(id, root) {
        override val triggers: List<String>
            get() = emptyList()

    }

    class Empty : Skill("", Configuration.empty()) {

        override val action: String = ""

        override val option: Option = EmptyOption()

    }

    class EmptyOption : Option(Configuration.empty()) {
        override val name: String = ""
        override val levelCap: Int = -1
        override val variables: List<Variable> = emptyList()
    }

}
