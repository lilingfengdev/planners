package com.bh.planners.core.storage

import com.bh.planners.Planners
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.database.ColumnBuilder
import taboolib.module.database.Host
import taboolib.module.database.SQL
import taboolib.module.database.SQLite
import java.util.*
import java.util.concurrent.CompletableFuture

interface Storage {

    companion object {

        val INSTANCE by lazy {
            when (type) {
                "SQL","MYSQL" -> StorageSQL()
                "LOCAL","YAML" -> StorageLocal()
                else -> error("None database source.")
            }
        }

        val userIdCache = mutableMapOf<UUID, Long>()

        val type: String
            get() = Planners.config.getString("database.use", "SQLITE")!!

        fun Player.toUserId(): Long {
            return INSTANCE.getUserId(this)
        }


        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            userIdCache.remove(e.player.uniqueId)
        }

        fun ColumnBuilder.id() {
            if (this is SQL) {
                id()
            } else if (this is SQLite) {
                id()
            }
        }

    }

    fun loadProfile(player: Player): PlayerProfile

    fun getUserId(player: Player): Long

    fun updateCurrentJob(profile: PlayerProfile)

    fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob>

    fun updateJob(player: Player, job: PlayerJob)

    fun createPlayerSkill(player: Player, job: PlayerJob, skill: Skill): CompletableFuture<PlayerJob.Skill>

    fun updateSkill(profile: PlayerProfile,skill: PlayerJob.Skill)

    fun update(profile: PlayerProfile)

    fun getDataContainer(player: Player): DataContainer
    fun getJob(player: Player, jobId: Long): PlayerJob
    fun getCurrentJobId(player: Player): Long?
}
