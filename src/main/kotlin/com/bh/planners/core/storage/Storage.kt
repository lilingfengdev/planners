package com.bh.planners.core.storage

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import java.util.*
import java.util.concurrent.CompletableFuture

interface Storage {

    companion object {

        val INSTANCE by lazy {
            StorageSQL()
        }

        val userIdCache = mutableMapOf<UUID, Long>()

        fun Player.toUserId(): Long {
            if (PlannersAPI.profiles.containsKey(uniqueId)) {
                return profile().id
            }
            return userIdCache.computeIfAbsent(uniqueId) {
                INSTANCE.getUserId(this)
            }
        }


    }

    fun loadProfile(player: Player): PlayerProfile

    fun getUserId(player: Player): Long

    fun updateCurrentJob(profile: PlayerProfile)

    fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob>

    fun updateJob(player: Player, job: PlayerJob)

    fun createPlayerSkill(player: Player, job: PlayerJob, skill: Skill): CompletableFuture<PlayerJob.Skill>

    fun updateSkill(skill: PlayerJob.Skill)
}
