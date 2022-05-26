package com.bh.planners.api

import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.event.PlayerProfileLoadEvent
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
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import java.util.*

object PlannersAPI {

    val routers = mutableListOf<Router>()

    val skills = mutableListOf<Skill>()

    val jobs = mutableListOf<Job>()

    val profiles = mutableMapOf<UUID, PlayerProfile>()

    val keySlots = mutableListOf<IKeySlot>()

    val Player.plannersProfile: PlayerProfile
        get() = profiles[uniqueId]!!

    val Player.plannersProfileIsLoaded: Boolean
        get() = profiles.containsKey(uniqueId)

    fun castSkill(player: Player, skillName: String) {
        player.plannersProfile.castSkill(skillName)
    }

    fun castSkill(player: Player, skill: Skill) {
        player.plannersProfile.castSkill(skill)
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

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        profiles.remove(e.player.uniqueId)
    }

}
