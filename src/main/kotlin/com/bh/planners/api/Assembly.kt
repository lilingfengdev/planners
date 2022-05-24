package com.bh.planners.api

import com.bh.planners.api.event.PlayerProfileLoadEvent
import com.bh.planners.core.storage.Storage
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

object Assembly {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        PlannersAPI.profiles[e.player.uniqueId] = Storage.INSTANCE.loadProfile(e.player)
    }

}