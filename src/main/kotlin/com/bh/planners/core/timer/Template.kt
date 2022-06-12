package com.bh.planners.core.timer

import taboolib.library.configuration.ConfigurationSection


open class Template(val id: String, var root: ConfigurationSection) {

    open val triggers = root.getStringList("__option__.triggers")

    open val async = root.getBoolean("__option__.async")

    open val action = root.getString("action", "")!!


}
