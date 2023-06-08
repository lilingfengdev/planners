package com.bh.planners.core.pojo.level

import taboolib.library.configuration.ConfigurationSection

class LevelOption(val algorithm: Algorithm, val min: Int, val root: ConfigurationSection) {

    val id = root.name

    fun toLevel(level: Int, experience: Int) = Level(algorithm, level.coerceAtLeast(min), experience)

}
