package com.bh.planners.core.pojo

import taboolib.library.configuration.ConfigurationSection

open class Condition(val option: ConfigurationSection) {

    val condition = option.getString("if")!!
    val indexTo = option.getInt("$")
    val consume = option.getString("consume")
    val placeholder = option.getString("placeholder")

}