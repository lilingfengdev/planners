package com.bh.planners.core.storage

import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

class StorageLocal : Storage {




    override fun loadProfile(player: Player): PlayerProfile {
        TODO("Not yet implemented")
    }

    override fun getUserId(player: Player): Long {
        TODO("Not yet implemented")
    }

    override fun updateCurrentJob(profile: PlayerProfile) {
        TODO("Not yet implemented")
    }

    override fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob> {
        TODO("Not yet implemented")
    }

    override fun updateJob(player: Player, job: PlayerJob) {
        TODO("Not yet implemented")
    }

    override fun createPlayerSkill(player: Player, job: PlayerJob, skill: Skill): CompletableFuture<PlayerJob.Skill> {
        TODO("Not yet implemented")
    }

    override fun updateSkill(skill: PlayerJob.Skill) {
        TODO("Not yet implemented")
    }

    override fun update(profile: PlayerProfile) {
        TODO("Not yet implemented")
    }

    override fun getDataContainer(player: Player): DataContainer {
        TODO("Not yet implemented")
    }
}