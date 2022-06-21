package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerProfileLoadEvent
import com.bh.planners.core.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object Assembly {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        PlannersAPI.profiles[e.player.uniqueId] = Storage.INSTANCE.loadProfile(e.player)
    }

    @Awake(LifeCycle.ENABLE)
    fun autoSave() {
        submit(async = true, period = PlannersOption.autoSaveFlagPeriod, delay = PlannersOption.autoSaveFlagPeriod) {
            saveAll()
        }
    }

    @Awake(LifeCycle.DISABLE)
    fun saveAll() {
        Bukkit.getOnlinePlayers().forEach { save(it) }
    }

    fun save(player: Player) {
        Storage.INSTANCE.update(player.plannersProfile)
    }


}