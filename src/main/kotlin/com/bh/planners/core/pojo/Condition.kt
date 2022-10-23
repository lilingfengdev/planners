package com.bh.planners.core.pojo

import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection

open class Condition(val option: ConfigurationSection) {

    val condition = option.getString("if")!!

    val indexTo = if (option.isList("$")) {
        option.getIntegerList("$")
    } else if (option.getString("$")!!.contains("-")) {
        val split = option.getString("$")!!.split("-")
        (Coerce.toInteger(split[0])..Coerce.toInteger(split[1])).map { it }
    } else listOf(option.getInt("$"))

    val consume = option.getString("consume")
    val placeholder = option.getString("placeholder")

}