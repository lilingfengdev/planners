package com.bh.planners.core.feature.damageable

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.mapValue
import taboolib.module.kether.KetherShell
import taboolib.module.kether.parseKetherScript
import taboolib.module.kether.runKether
import java.io.File

class DamageableModel(val file: File) {

    val config = Configuration.loadFromFile(file)
    val id = file.nameWithoutExtension

    val attackStreams = config.mapValue("stream-attack") {
        Stream(it, Type.ATTACK)
    }.values.sortedBy { it.sort }

    val defendStreams = config.mapValue("stream-defend") {
        Stream(it, Type.DEFEND)
    }.values.sortedBy { it.sort }

    class Stream(conf: ConfigurationSection, val type: Type) {

        val sort = conf.getInt("sort", 999)
        val name = conf.getString("name", "@Stream:@$sort")
        val data = conf.getString("data")
        val condition = conf.getString("condition", "true")!!
        val preAction = conf.getString("pre-action")
        val action = conf.getString("action", "")!!
        val postAction = conf.getString("post-action")

    }

    enum class Type {
        ATTACK, DEFEND
    }

}