package com.bh.planners.api

import com.bh.planners.api.compat.chemdah.FailureSkillCast
import com.bh.planners.api.compat.chemdah.PostSkillCast
import com.bh.planners.api.compat.chemdah.PreSkillCast
import com.bh.planners.api.compat.chemdah.RecordSkillCast
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import com.bh.planners.util.files
import com.bh.planners.util.register
import ink.ptms.chemdah.core.quest.objective.ObjectiveCountableI
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
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
            PlannersAPI.jobs += Job(it.toYamlName(), Configuration.loadFromFile(it))
        }
    }

    fun File.toYamlName(): String {
        return name.replace(".yml", "")
    }

    @Awake(LifeCycle.ENABLE)
    fun loadSkills() {
        PlannersAPI.skills.clear()
        files("skill", listOf("skill_def0.yml")) {
            PlannersAPI.skills += Skill(it.toYamlName(), Configuration.loadFromFile(it))
        }
        ScriptLoader.autoLoad()
        // 注册Chemdah任务
        listOf<ObjectiveCountableI<*>>(PreSkillCast,PostSkillCast,RecordSkillCast,FailureSkillCast).register()
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
