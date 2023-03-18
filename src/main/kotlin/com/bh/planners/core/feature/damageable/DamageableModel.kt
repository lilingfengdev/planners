package com.bh.planners.core.feature.damageable

import com.bh.planners.util.mapListAs
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import java.io.File
import java.util.UUID

class DamageableModel(val file: File) {

    val config = Configuration.loadFromFile(file)
    val id = file.nameWithoutExtension

    val streamSize: Int
        get() = attackStreams.size + defendStreams.size

    val attackStreams = config.mapListAs("stream-attack") {
        Stream(it, Type.ATTACK)
    }.sortedBy { it.sort }

    val defendStreams = config.mapListAs("stream-defend") {
        Stream(it, Type.DEFEND)
    }.sortedBy { it.sort }

    class Stream(conf: ConfigurationSection, val type: Type) {

        val id = conf.getString("id",UUID.randomUUID().toString())

        val sort = conf.getInt("sort", 999)
        val name = conf.getString("name", "@Stream:@$sort")
        val data = conf.getString("data")
        val condition = conf.getString("condition", "true")!!
        val preAction = conf.getString("pre-action", "1")!!
        val action = conf.getString("action", "1")!!
        val postAction = conf.getString("post-action", "1")!!

        var actionBuffer : String

        init {
            val builder = StringBuilder()
            if (condition != "true") {
                builder.append("if $condition then {${System.lineSeparator()}")
            }
            builder.append(action)
            if (condition != "true") {
                builder.append("${System.lineSeparator()}}")
            }
            actionBuffer = builder.toString()
        }


        override fun toString(): String {
            return "Stream(type=$type, id=$id, sort=$sort, name=$name, data=$data, condition='$condition', preAction='$preAction', action='$action', postAction='$postAction', actionBuffer='$actionBuffer')"
        }


    }

    enum class Type {
        ATTACK, DEFEND
    }

}