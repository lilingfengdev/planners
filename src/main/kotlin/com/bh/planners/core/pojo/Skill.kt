package com.bh.planners.core.pojo

import com.bh.planners.core.feature.presskey.Emitter.type
import com.bh.planners.core.timer.Template
import com.bh.planners.util.getAction
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

open class Skill(val key: String, val config: ConfigurationSection) {

    open val option = Option(config.getConfigurationSection("__option__") ?: config.createSection("__option__"))

    open lateinit var actionMode: ActionMode

    open val action = getAction(config.getString("action", "")!!) {
        actionMode = it
    }

    open class Option(val root: ConfigurationSection) {

        open val name = root.getString("name", root.name)!!
        open val levelCap = root.getInt("level-cap", 5)
        open val async = root.getBoolean("async", false)
        open val isBind = root.getBoolean("bind", false)
        open val isNatural = root.getBoolean("natural", false)
        val attribute = Attribute(root.getConfigurationSection("attribute") ?: Configuration.empty())

        open val upgradeConditions = root.getMapList("upgrade-condition").map {
            UpgradeCondition(Configuration.fromMap(it))
        }

        open val variables = root.getConfigurationSection("variables")?.getKeys(false)?.map {
            Variable(it, root.getString("variables.$it")!!)
        } ?: emptyList()

    }

    class Attribute(val root: ConfigurationSection) {

        val map = root.getKeys(false).map {
            it.split(",") to root.getStringList(it)
        }.toMap()

        val default = get("default") ?: emptyList()

        fun get(index: String): List<String>? {

            map.forEach {
                if (index in it.key) {
                    return it.value
                }
            }
            return null
        }

    }

    /**
     * 目的是为了以后有拓展不影响其他条件条目
     */
    class UpgradeCondition(option: ConfigurationSection) : Condition(option)

    class Variable(val key: String, val expression: String)

    class Empty : Skill("", Configuration.empty()) {

        override val action: String = ""

        override val option: Option = EmptyOption()

    }

    class EmptyOption : Option(Configuration.empty()) {
        override val name: String = ""
        override val levelCap: Int = -1
        override val variables: List<Variable> = emptyList()
    }

    enum class ActionMode {
        SIMPLE, DEFAULT
    }

}
