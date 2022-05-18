package com.bh.planners.api

import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object PlannersLoader {

    @Config("group.yml")
    lateinit var routerConfig: Configuration

    @Awake(LifeCycle.ENABLE)
    fun loadJobs() {
        PlannersAPI.jobs.clear()
        files("job", listOf("job_def0.yml", "job_def1.yml")) {
            PlannersAPI.jobs += Job(Configuration.loadFromFile(it))
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun loadSkills() {
        PlannersAPI.skills.clear()
        files("skill", listOf("skill_def0.yml", "skill_def1.yml")) {
            PlannersAPI.skills += Skill(Configuration.loadFromFile(it))
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun loadGroups() {
        PlannersAPI.routers.clear()
        routerConfig.reload()
        routerConfig.getKeys(false).forEach {
            PlannersAPI.routers += Router(routerConfig.getConfigurationSection(it)!!)
        }
    }


    fun files(path: String, defs: List<String>, callback: (File) -> Unit) {
        defs.forEach {
            releaseResourceFile("$path/$it")
        }
        getFiles(File(getDataFolder(), path)).forEach {
            callback(it)
        }
    }

    fun getFiles(file: File): List<File> {
        val listOf = mutableListOf<File>()
        when (file.isDirectory) {
            true -> listOf += file.listFiles().flatMap { getFiles(it) }
            false -> {
                if (file.name.endsWith(".yml")) {
                    listOf += file
                }
            }
        }
        return listOf
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        routerConfig.reload()
        this.loadGroups()
        this.loadJobs()
        this.loadSkills()
    }

}
