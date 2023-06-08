package com.bh.planners.util

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration

fun <T> ConfigurationSection.mapListAs(path: String, transform: (ConfigurationSection) -> T): MutableList<T> {
    return getMapList(path).map { transform(Configuration.fromMap(it)) }.toMutableList()
}