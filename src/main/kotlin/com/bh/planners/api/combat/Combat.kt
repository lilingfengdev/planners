package com.bh.planners.api.combat

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.api.updateFlag
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.data.Data
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.skill.bukkit.BukkitGrid
import com.bh.planners.core.skill.bukkit.BukkitGrid.isHandGrid
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.sendActionBar

object Combat {

    const val NAMESPACE = "@isCombat"

    @Config("combat.yml")
    lateinit var config: Configuration

    val Player.isCombat
        get() = plannersProfileIsLoaded && plannersProfile.getFlag(NAMESPACE)?.toBoolean() ?: false

    val Player.isCombatLocal: String
        get() = toCombatLocal(isCombat)

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

    fun Player.enableCombat() {
        plannersProfile.updateFlag(NAMESPACE, true)
    }

    fun Player.toggleCombat() {
        if (this.isCombat) {
            this.disableCombat()
        } else {
            this.enableCombat()
        }
    }

    fun Player.disableCombat() {
        plannersProfile.updateFlag(NAMESPACE, false)
    }

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


    fun toCombatLocal(value: Boolean): String {
        return config.getString("value-$value")!!
    }

    @Awake(LifeCycle.ENABLE)
    fun enableActionbarTask() {
        if (!isPlaceholderAPIEnable || !isActionbarEnable) return
        actionbarPlatformTask = submit(period = actionbarPeriod, async = true) {
            Bukkit.getOnlinePlayers().forEach {
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
        this.enableActionbarTask()
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerItemHeldEvent) {
        if (e.player.isCombat && !e.isCancelled) {
            e.isCancelled = true
            PlannersAPI.callKeyByGroup(e.player, (e.newSlot + 1).toString())
        }
    }


}