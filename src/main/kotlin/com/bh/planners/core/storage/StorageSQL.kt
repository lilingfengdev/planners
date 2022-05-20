package com.bh.planners.core.storage

import com.bh.planners.Planners
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage.Companion.toUserId
import jdk.nashorn.internal.scripts.JO
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.sql.DataSource

class StorageSQL : Storage {

    val host = Planners.config.getHost("database.sql")

    companion object {
        const val ID = "id"
        const val UUID = "uuid"
        const val JOB = "job"
        const val USER = "user"
        const val DATA = "data"

        const val MANA = "mana"
        const val SKILL = "skill"
        const val LEVEL = "level"
        const val EXPERIENCE = "experience"

        const val SLOT_KEY = "slot"

    }


    val userTable = Table("planners_user", host) {
        add("id") { id() }
        add(UUID) { type(ColumnTypeSQL.VARCHAR, 36) }
        add(MANA) {
            type(ColumnTypeSQL.DECIMAL, 20) {
                def(0.0)
            }
        }
        add(JOB) { type(ColumnTypeSQL.INT) }
        add(DATA) {
            type(ColumnTypeSQL.LONGTEXT)
        }
    }

    val jobTable = Table("planners_job", host) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQL.INT, 10) }
        add(JOB) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(LEVEL) {
            type(ColumnTypeSQL.INT, 30) {
                def(1)
            }
        }
        add(EXPERIENCE) {
            type(ColumnTypeSQL.INT) {
                def(0)
            }
        }
    }

    val skillTable = Table("planners_skill", host) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQL.INT, 10) }
        add(JOB) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(SKILL) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(LEVEL) {
            type(ColumnTypeSQL.INT, 10) {
                def(0)
            }
        }
    }

    val keySlot = Table("planners_key_slot", host) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQL.INT, 10) }
        add(SLOT_KEY) { type(ColumnTypeSQL.VARCHAR, 10) }
        add(SKILL) { type(ColumnTypeSQL.INT) }
    }

    val dataSource by lazy { host.createDataSource() }

    init {
        userTable.createTable(dataSource)
        jobTable.createTable(dataSource)
        skillTable.createTable(dataSource)
        keySlot.createTable(dataSource)
    }


    override fun loadProfile(player: Player): PlayerProfile {
        val userId = player.toUserId()
        val profile = PlayerProfile(player, userId)

        // 获取职业对应技能
        getCurrentJobId(player)?.let { jobId ->
            if (jobId != 0L) {
                profile.job = getJob(player, jobId)
            }
        }

        // 初始化metadata
        profile.dataContainer.merge(getDataContainer(player))

        return profile
    }


    private fun getDataContainer(player: Player): DataContainer {
        return userTable.select(dataSource) {
            where { ID eq player.toUserId() }
            rows(DATA)
        }.first { DataContainer() }
    }

    fun getCurrentJobId(player: Player): Long? {
        return userTable.select(dataSource) {
            where { ID eq player.toUserId() }
            rows(JOB)
        }.firstOrNull { getLong(JOB) }
    }

    fun getJob(player: Player, jobId: Long): PlayerJob {
        return jobTable.select(dataSource) {
            where {
                ID eq jobId
            }
            rows(JOB, LEVEL, EXPERIENCE)
        }.first {
            PlayerJob(jobId, getString(JOB), getInt(LEVEL), getInt(EXPERIENCE)).also {
                it.skills += getSkills(player, it.jobKey)
            }
        }
    }

    override fun createPlayerSkill(player: Player, job: PlayerJob, skill: Skill): CompletableFuture<PlayerJob.Skill> {
        val future = CompletableFuture<PlayerJob.Skill>()
        skillTable.insert(dataSource, USER, JOB, SKILL, LEVEL) {
            value(player.toUserId(), job.jobKey, skill.key, 0)
            onFinally {
                val id = generatedKeys.run {
                    next()
                    Coerce.toLong(getObject(1))
                }
                future.complete(PlayerJob.Skill(id, skill.key, 0))
            }
        }
        return future
    }

    override fun updateSkill(skill: PlayerJob.Skill) {
        skillTable.update(dataSource) {
            where {
                ID eq skill.id
            }
            set(LEVEL, skill.level)
        }
    }

    fun getSkills(player: Player, jobKey: String): List<PlayerJob.Skill> {
        val userId = player.toUserId()
        return skillTable.select(dataSource) {
            USER eq userId
            JOB eq jobKey
            rows(ID, SKILL, LEVEL)
        }.map {
            PlayerJob.Skill(getLong(ID), getString(SKILL), getInt(LEVEL))
        }
    }

    override fun getUserId(player: Player): Long {

        if (!userTable.find(dataSource) { where { UUID eq player.uniqueId.toString() } }) {
            userTable.insert(dataSource, UUID) {
                value(player.uniqueId.toString())
            }
        }

        return userTable.select(dataSource) {
            where { UUID eq player.uniqueId.toString() }
            rows(ID)
        }.first { getLong(ID) }
    }

    override fun updateCurrentJob(profile: PlayerProfile) {
        userTable.update(dataSource) {
            where { ID eq profile.id }
            set(JOB, profile.job?.id)
        }
    }

    override fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob> {
        val minLevel = job.option.counter.min
        val future = CompletableFuture<PlayerJob>()
        jobTable.insert(dataSource, USER, JOB, LEVEL, EXPERIENCE) {
            value(player.toUserId(), job.key, minLevel, 0.0)
            onFinally {
                val id = generatedKeys.run {
                    next()
                    Coerce.toLong(getObject(1))
                }
                future.complete(PlayerJob(id, job.key, minLevel, 0))
            }
        }
        return future
    }

    override fun updateJob(player: Player, job: PlayerJob) {
        jobTable.update(dataSource) {
            where { ID eq job.id }
            set(LEVEL, job.counter.level)
            set(EXPERIENCE, job.counter.experience)
        }
    }

}
