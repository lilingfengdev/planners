package com.bh.planners.api

import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import java.util.*

object PlannersAPI {

    val routers = mutableListOf<Router>()

    val skills = mutableListOf<Skill>()

    val jobs = mutableListOf<Job>()

    val profiles = mutableMapOf<UUID, PlayerProfile>()

    fun Player.profile(): PlayerProfile {
        return profiles.computeIfAbsent(uniqueId) {
            Storage.INSTANCE.loadProfile(this).get()
        }
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        submit(async = true) { e.player.profile() }
    }

}
