package com.bh.planners.core.feature

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.hasJob
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.feature.grid.BukkitGrid
import com.bh.planners.core.feature.grid.BukkitGrid.isHandGrid
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.sendActionBar

object Combat {

    const val NAMESPACE = "@isCombat"

    @Config("combat.yml")
    lateinit var config: Configuration

    val isPlaceholderAPIEnable by lazy {
        Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
    }

    val isActionbarEnable: Boolean
        get() = config.getBoolean("actionbar.enable", false)

    val actionbarMessage: String
        get() = config.getString("actionbar.message", "")!!

    val actionbarPeriod: Long
        get() = config.getLong("actionbar.period", 20)

    var actionbarPlatformTask: PlatformExecutor.PlatformTask? = null

    val Player.actionbarMessage: String?
        get() = plannersProfile.job?.toActionbarMessage()

    fun PlayerJob.toActionbarMessage(): String? {
        return instance.toActionbarMessage()
    }

    fun Job.toActionbarMessage(): String? {
        val actionbar = option.actionbar ?: return null
        return if (actionbar.startsWith("extend")) {
            PlannersAPI.getJob(actionbar.replaceFirst("extend ", "")).toActionbarMessage()
        } else {
            actionbar
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun enableActionbarTask() {
        if (!isPlaceholderAPIEnable || !isActionbarEnable) return
        actionbarPlatformTask = submit(period = actionbarPeriod, async = true) {
            Bukkit.getOnlinePlayers().forEach {
                if (it.hasJob) return@forEach
                // 如果手持技能icon
                if (it.isHandGrid) {
                    it.sendActionBar(BukkitGrid.toActionbarValue(it))
                } else {
                    it.sendActionBar(PlaceholderAPI.setPlaceholders(it, it.actionbarMessage ?: actionbarMessage))
                }

            }
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        config.reload()
        actionbarPlatformTask?.cancel()
        enableActionbarTask()
    }

}