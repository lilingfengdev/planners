package com.bh.planners.api

import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import java.util.*

object PlannersAPI {

    val routers = mutableListOf<Router>()

    val skills = mutableListOf<Skill>()

    val jobs = mutableListOf<Job>()

    val profiles = mutableMapOf<UUID, PlayerProfile>()

    fun Player.profile(): PlayerProfile {
        return profiles.computeIfAbsent(uniqueId) {
            PlayerProfile(this).also { Storage.INSTANCE.loadInto(it) }
        }
    }

}
