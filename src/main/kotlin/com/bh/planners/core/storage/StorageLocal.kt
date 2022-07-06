package com.bh.planners.core.storage

import com.bh.planners.api.hasJob
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage.Companion.toUserId
import com.bh.planners.util.generatorId
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.common5.Coerce
import taboolib.module.configuration.Configuration
import java.util.UUID
import java.util.concurrent.CompletableFuture

class StorageLocal : Storage {

    val cache = mutableMapOf<UUID, Configuration>()

    override fun loadProfile(player: Player): PlayerProfile {
        return PlayerProfile(player, player.toUserId())
    }

    override fun getUserId(player: Player): Long {
        val profileFile = getProfileFile(player)
        return if (profileFile.contains("id")) {
            profileFile.getLong("id")
        } else {
            generatorId().apply {
                profileFile["id"] = this
                save(profileFile)
            }
        }
    }

    override fun getJob(player: Player, jobId: Long): PlayerJob {
        val config = getProfileFile(player)
        val jobKey = config.getString("job.${jobId}.key")!!
        val level = config.getInt("job.${jobId}.level")
        val experience = config.getInt("job.${jobId}.experience")
        val points = config.getInt("job.${jobId}.points")
        return PlayerJob(jobId, jobKey, level, experience).also {
            it.skills += getSkills(player, jobId)
            it.point = points
        }
    }

    fun getSkills(player: Player, jobId: Long): List<PlayerJob.Skill> {
        val config = getProfileFile(player)
        return config.getConfigurationSection("job.${jobId}.skill")?.getKeys(false)?.map {
            val node = config.getConfigurationSection("job.${jobId}.skill.$it")!!
            PlayerJob.Skill(
                Coerce.toLong(it),
                node.getString("key")!!,
                node.getInt("level"),
                node.getString("shortcut")
            )
        } ?: emptyList()
    }

    override fun getCurrentJobId(player: Player): Long? {
        val config = getProfileFile(player)
        return config.getLong("current-job")
    }

    override fun updateCurrentJob(profile: PlayerProfile) {
        at(profile.player) {
            this["current-job"] = profile.job?.id
        }
    }

    override fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob> {
        val minLevel = job.router.counter.min
        val id = generatorId()
        at(player) {
            this["job.$id.key"] = job.key
            this["job.$id.level"] = minLevel
        }
        return CompletableFuture.completedFuture(PlayerJob(id, job.key, minLevel, 0))
    }

    override fun updateJob(player: Player, job: PlayerJob) {
        at(player) {
            this["job.${job.id}.job"] = job.jobKey
            this["job.${job.id}.level"] = job.level
            this["job.${job.id}.experience"] = job.experience
            this["job.${job.id}.point"] = job.point
        }
    }

    override fun createPlayerSkill(player: Player, job: PlayerJob, skill: Skill): CompletableFuture<PlayerJob.Skill> {
        val generatorId = generatorId()
        at(player) {
            this["job.${job.id}.skill.$generatorId.key"] = skill.key
            this["job.${job.id}.skill.$generatorId.level"] = 0
        }
        return CompletableFuture.completedFuture(PlayerJob.Skill(generatorId, skill.key, 0, null))
    }

    override fun updateSkill(profile: PlayerProfile, skill: PlayerJob.Skill) {
        at(profile.player) {
            this["job.${profile.job?.id}.skill.${skill.id}.level"] = skill.level
            this["job.${profile.job?.id}.skill.${skill.id}.shortcut"] = skill.shortcutKey
        }
    }

    override fun update(profile: PlayerProfile) {
        at(profile.player) {
            this["data"] = profile.flags.toJson()
            updateJob(profile.player, profile.job ?: return@at)
        }
    }

    override fun getDataContainer(player: Player): DataContainer {
        return DataContainer.fromJson(getProfileFile(player).getString("flags", "{}")!!)
    }

    fun save(player: Player) {
        save(getProfileFile(player))
    }

    fun save(configuration: Configuration) {
        configuration.saveToFile()
    }

    fun at(player: Player, call: Configuration.() -> Unit) {
        val configuration = getProfileFile(player)
        call(configuration)
        save(getProfileFile(player))
    }

    fun getProfileFile(player: Player): Configuration {
        val uniqueId = player.uniqueId
        return cache.computeIfAbsent(uniqueId) {
            Configuration.loadFromFile(newFile(getDataFolder(), "/data/$uniqueId.json"))
        }

    }

}