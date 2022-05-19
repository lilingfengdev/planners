package com.bh.planners.api

import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import com.bh.planners.util.files
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import java.io.File

object PlannersLoader {

    @Config("router.yml")
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
        files("skill", listOf("skill_def0.yml")) {
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



    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        routerConfig.reload()
        this.loadGroups()
        this.loadJobs()
        this.loadSkills()
    }

}
