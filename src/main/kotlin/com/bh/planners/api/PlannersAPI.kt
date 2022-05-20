package com.bh.planners.api

import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.key.KeySlot
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

    val keySlots = mutableListOf<IKeySlot>()

    fun Player.profile(): PlayerProfile {
        return profiles.computeIfAbsent(uniqueId) {
            Storage.INSTANCE.loadProfile(this)
        }
    }

    fun PlayerProfile.castSkill(skillName: String) {
        castSkill(skills.firstOrNull { it.key == skillName } ?: return)
    }

    fun PlayerProfile.castSkill(skill: Skill) {
        // 扣除法力值
        takeMana(skill)
        // 缓存释放技能
        val session = Session(player, skill)
        session.cast()
    }
}
