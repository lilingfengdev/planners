package com.bh.planners.core.storage

import com.bh.planners.Planners
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
import javax.sql.DataSource

class StorageSQL : Storage {

    val host = Planners.config.getHost("database.sql")
    val userIdCache = mutableMapOf<UUID, Long>()

    companion object {
        const val ID = "id"
        const val UUID = "uuid"
        const val CURRENT_JOB = "current_job"
        const val JOB = "job"
        const val USER = "user"
        const val DATA = "data"

        const val MANA = "mana"
        const val SKILL = "skill"
        const val LEVEL = "level"
        const val EXPERIENCE = "experience"

    }


    val userTable = Table("planners_user_info", host) {
        add("id") { id() }
        add(UUID) { type(ColumnTypeSQL.VARCHAR, 36) }
        add(MANA) {
            type(ColumnTypeSQL.DECIMAL, 20) {
                def(0.0)
            }
        }
        add(CURRENT_JOB) {
            type(ColumnTypeSQL.VARCHAR, 30) {
                def(null)
            }
        }
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

    val dataSource by lazy { host.createDataSource() }

    override fun loadProfile(player: Player): CompletableFuture<PlayerProfile> {
        return getUserId(player).thenApply {
            val profile = PlayerProfile(player, it)

            // 获取职业对应技能
            getCurrentJob(player)?.let { jobKey ->
                profile.job = getJob(player, jobKey)
            }

            // 初始化metadata
            profile.dataContainer.merge(getDataContainer(player))

            profile
        }
    }

    private fun getDataContainer(player: Player): DataContainer {
        return userTable.select(dataSource) {
            where { ID eq player.toUserId() }
            rows(DATA)
        }.first { DataContainer() }
    }

    fun getCurrentJob(player: Player): String? {
        return userTable.select(dataSource) {
            where { UUID eq player.uniqueId.toString() }
            rows(CURRENT_JOB)
        }.firstOrNull { getString(CURRENT_JOB) }
    }

    fun getJob(player: Player, jobKey: String): PlayerJob {
        val userId = player.toUserId()
        return jobTable.select(dataSource) {
            where {
                USER eq userId
                JOB eq jobKey
            }
            rows(LEVEL, EXPERIENCE)
        }.first {
            PlayerJob(jobKey, getInt(LEVEL), getInt(EXPERIENCE)).also {
                it.skills += getSkills(player, jobKey)
            }
        }
    }

    fun getSkills(player: Player, jobKey: String): List<PlayerJob.Skill> {
        val userId = player.toUserId()
        return skillTable.select(dataSource) {
            USER eq userId
            JOB eq jobKey
            rows(SKILL, LEVEL)
        }.map {
            PlayerJob.Skill(getString(SKILL), getInt(LEVEL))
        }
    }

    override fun getUserId(player: Player): CompletableFuture<Long> {
        val future = CompletableFuture<Long>()
        if (userIdCache.containsKey(player.uniqueId)) {
            future.complete(userIdCache[player.uniqueId]!!)
        } else if (userTable.find(dataSource) { where { UUID eq player.uniqueId.toString() } }) {
            userTable.select(dataSource) {
                where { UUID eq player.uniqueId.toString() }
                rows(ID)
            }.first {
                future.complete(getLong(ID).also { userIdCache[player.uniqueId] = it })
            }
        }
        userTable.insert(dataSource, UUID) {
            value(player.uniqueId.toString())
            onFinally {
                generatedKeys.next()
                future.complete(Coerce.toLong(generatedKeys.getObject(1)))
            }
        }
        return future
    }
}
