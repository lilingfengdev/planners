package com.bh.planners.core.storage

import com.bh.planners.Planners
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.data.DataContainer
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage.Companion.toUserId
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*
import java.util.concurrent.CompletableFuture

open class StorageSQL : Storage {

    val host = Planners.config.getHost("database.sql")

    companion object {

        val userIdCache = mutableMapOf<UUID, Long>()

        const val ID = "id"
        const val UUID = "uuid"
        const val JOB = "job"
        const val USER = "user"
        const val DATA = "data"

        const val MANA = "mana"
        const val SKILL = "skill"
        const val LEVEL = "level"
        const val EXPERIENCE = "experience"
        const val POINT = "point"

        const val SHORTCUT_KEY = "shortcut_key"

    }

    val userTable: Table<*, *> = Table("planners_user", host) {
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

    val jobTable: Table<*, *> = Table("planners_job", host) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQL.INT, 10) }
        add(JOB) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(POINT) {
            type(ColumnTypeSQL.INT) {
                def(0)
            }
        }
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

    val skillTable: Table<*, *> = Table("planners_skill", host) {
        add("id") { id() }
        add(USER) { type(ColumnTypeSQL.INT, 10) }
        add(JOB) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(SKILL) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(SHORTCUT_KEY) { type(ColumnTypeSQL.VARCHAR, 30) }
        add(LEVEL) {
            type(ColumnTypeSQL.INT, 10) {
                def(0)
            }
        }
    }

    val dataSource by lazy { host.createDataSource() }

    init {
        userTable.createTable(dataSource)
        jobTable.createTable(dataSource)
        skillTable.createTable(dataSource)
    }


    override fun loadProfile(player: Player): PlayerProfile {
        val userId = player.toUserId()
        return PlayerProfile(player, userId)
    }


    override fun getDataContainer(player: Player): DataContainer {
        return userTable.select(dataSource) {
            where { ID eq player.toUserId() }
            rows(DATA)
        }.firstOrNull {
            DataContainer.fromJson(getString(DATA) ?: "{}")
        } ?: DataContainer()
    }

    override fun getCurrentJobId(player: Player): Long? {
        return userTable.select(dataSource) {
            where { ID eq player.toUserId() }
            rows(JOB)
        }.firstOrNull { getLong(JOB) }
    }

    override fun getJob(player: Player, jobId: Long): PlayerJob {
        return jobTable.select(dataSource) {
            where {
                ID eq jobId
            }
            rows(JOB, LEVEL, EXPERIENCE, POINT)
        }.first {
            PlayerJob(jobId, getString(JOB), getInt(LEVEL), getInt(EXPERIENCE)).also {
                it.skills += getSkills(player, it.jobKey)
                it.point = getInt(POINT)
            }
        }
    }

    override fun getJob(player: Player, id: String): PlayerJob {
        return jobTable.select(dataSource) {
            where {
                JOB eq id
            }
            rows(ID, LEVEL, EXPERIENCE, POINT)
        }.first {
            PlayerJob(getLong(ID), id, getInt(LEVEL), getInt(EXPERIENCE)).also {
                it.skills += getSkills(player, it.jobKey)
                it.point = getInt(POINT)
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
                future.complete(PlayerJob.Skill(id, skill.key, 0, null))
            }
        }
        return future
    }

    override fun updateSkillJob(player: Player, job: PlayerJob, skill: PlayerJob.Skill) {
        skillTable.update(dataSource) {
            where { ID eq skill.id }
            set(JOB, job.jobKey)
        }
    }

    override fun updateSkill(profile: PlayerProfile, skill: PlayerJob.Skill) {
        if (skill.id == -1L) return
        skillTable.update(dataSource) {
            where {
                ID eq skill.id
            }
            set(LEVEL, skill.level)
            set(SHORTCUT_KEY, skill.shortcutKey)
        }
    }

    fun getSkills(player: Player, jobKey: String): List<PlayerJob.Skill> {
        val userId = player.toUserId()
        return skillTable.select(dataSource) {
            where {
                USER eq userId
                JOB eq jobKey
            }
            rows(ID, SKILL, LEVEL, SHORTCUT_KEY)
        }.map {
            PlayerJob.Skill(getLong(ID), getString(SKILL), getInt(LEVEL), getString(SHORTCUT_KEY))
        }
    }

    override fun getUserId(player: Player): Long {
        if (userIdCache.containsKey(player.uniqueId)) {
            return userIdCache[player.uniqueId]!!
        }

        val userId = userTable.select(dataSource) {
            where { UUID eq player.uniqueId.toString() }
            rows(ID)
        }.firstOrNull { getLong(ID) } ?: -1L

        if (userId == -1L) {
            userTable.insert(dataSource, UUID) {
                value(player.uniqueId.toString())
            }
            return getUserId(player)
        }

        userIdCache[player.uniqueId] = userId
        return userId
    }

    override fun updateCurrentJob(profile: PlayerProfile) {
        userTable.update(dataSource) {
            where { ID eq profile.id }
            set(JOB, profile.job?.id)
        }
    }

    override fun update(profile: PlayerProfile) {
        userTable.update(dataSource) {
            where { ID eq profile.id }
            set(DATA, profile.flags.toJson())
        }
        updateJob(profile.player, profile.job ?: return)
    }

    override fun createPlayerJob(player: Player, job: Job): CompletableFuture<PlayerJob> {
        val minLevel = job.router.counter.min
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
            set(JOB, job.jobKey)
            set(LEVEL, job.level)
            set(EXPERIENCE, job.experience)
            set(POINT, job.point)
        }
    }

}
