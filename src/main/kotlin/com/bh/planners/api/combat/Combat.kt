package com.bh.planners.api.combat

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.api.getFlag
import com.bh.planners.api.setFlag
import com.bh.planners.core.pojo.data.Data
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerItemHeldEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.onlinePlayers
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.util.sendActionBar

object Combat {

    @Config("combat.yml")
    lateinit var config: Configuration

    val Player.isCombat
        get() = plannersProfileIsLoaded && plannersProfile.getFlag("@isCombat")?.toBoolean() ?: false

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
        plannersProfile.setFlag("@issCombat", Data(true, System.currentTimeMillis()))
    }

    fun toCombatLocal(value: Boolean): String {
        return config.getString("value-$value")!!
    }

    @Awake(LifeCycle.ENABLE)
    fun enableActionbarTask() {
        info(isPlaceholderAPIEnable, isActionbarEnable)
        if (!isPlaceholderAPIEnable || !isActionbarEnable) return
        actionbarPlatformTask = submit(period = actionbarPeriod, async = true) {
            Bukkit.getOnlinePlayers().forEach {
                it.sendActionBar(PlaceholderAPI.setPlaceholders(it, actionbarMessage))
            }
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        config.reload()
        actionbarPlatformTask?.cancel()
        this.enableActionbarTask()
    }

    fun Player.disableCombat() {
        plannersProfile.setFlag("@issCombat", Data(false, System.currentTimeMillis()))
    }

    @SubscribeEvent
    fun e(e: PlayerItemHeldEvent) {
        if (e.player.isCombat) {

        }
    }


}