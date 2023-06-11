package com.bh.planners.core.timer

import com.bh.planners.util.getScriptFactor
import taboolib.library.configuration.ConfigurationSection


open class Template(val id: String, var root: ConfigurationSection) {

    open val triggers = root.getStringList("__option__.triggers")

    open val async = root.getBoolean("__option__.async")

    open val script = getScriptFactor(root.getString("action", "")!!)

    open val keys = root.getStringList("__option__.key").map { it.toUpperCase() }.toList()

}
