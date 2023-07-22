package com.bh.planners.api

import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.data.DataContainer.Companion.fromJson
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.File
import java.nio.charset.StandardCharsets

object GlobalVarTable {

    val data = DataContainer()

    fun get(key: String): Data? {
        return data[key]
    }

    fun set(key: String, value: Any, timeout: Long) {
        data[key] = Data(value, survivalStamp = timeout)
    }

    fun keys(): List<String> = data.keys()

    @Schedule(period = (20 * 60 * 30).toLong(), delay = (20 * 60 * 30).toLong())
    @Awake(LifeCycle.DISABLE)
    fun saveAll() {
        val file = File(getDataFolder(), "data.json")
        if (file.exists()) {
            file.createNewFile()
        }
        Configuration.loadFromString(data.toJson(), Type.JSON).saveToFile(file)
    }

    @Awake(LifeCycle.ENABLE)
    fun loadAll() {
        val file = File(getDataFolder(), "data.json")
        if (file.exists()) {
            data.merge(fromJson(file.readText(StandardCharsets.UTF_8)))
        }
    }

}