package com.bh.planners.api

import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerProfileLoadEvent
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.storage.Storage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Schedule
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object Assembly {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        val profile = Storage.INSTANCE.loadProfile(e.player)
        PlannersAPI.profiles[e.player.uniqueId] = profile
        PlayerInitializeEvent(e.player, profile).call()
    }

    @Awake(LifeCycle.ENABLE)
    fun autoSave() {
        submit(async = true, period = PlannersOption.autoSaveFlagPeriod, delay = PlannersOption.autoSaveFlagPeriod) {
            saveAll()
        }
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        save(e.player)
        PlannersAPI.profiles.remove(e.player.uniqueId)
    }

    @Awake(LifeCycle.DISABLE)
    fun saveAll() {
        Bukkit.getOnlinePlayers().forEach { save(it) }
    }

    fun save(player: Player) {
        Storage.INSTANCE.update(player.plannersProfile)
    }

    @SubscribeEvent
    fun e(e: PlayerSelectedJobEvent) {
        if (e.profile.hasJob) {
            e.profile.getSkills().filter { it.level == 0 && it.instance.option.isNatural }.forEach {
                it.level = 1
                Storage.INSTANCE.updateSkill(it)
            }
        }
    }


}