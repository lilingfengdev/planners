package com.bh.planners.api

import com.bh.planners.api.ManaCounter.takeMana
import com.bh.planners.api.counter.Counting
import com.bh.planners.api.enums.ExecuteResult
import com.bh.planners.api.event.PlayerCastSkillEvent
import com.bh.planners.api.event.PlayerKeydownEvent
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.pojo.Job
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.key.IKeySlot
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.common5.Coerce
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

    fun cast(player: Player, skillName: String, mark: Boolean = true): ExecuteResult {
        return player.plannersProfile.cast(skillName, mark)
    }

    fun cast(player: Player, skill: Skill, mark: Boolean = true): ExecuteResult {
        return player.plannersProfile.cast(skill, mark)
    }

    fun PlayerProfile.cast(skillName: String, mark: Boolean = true): ExecuteResult {
        return cast(skills.firstOrNull { it.key == skillName } ?: error("Skill '${skillName}' not found."), mark)
    }

    fun callKeyByGroup(player: Player, keyGroup: String) {
        this.callKey(player, keySlots.firstOrNull { it.group == keyGroup } ?: return)
    }

    fun callKeyById(player: Player, keyId: String) {
        this.callKey(player, keySlots.firstOrNull { it.key == keyId } ?: return)
    }

    fun callKey(player: Player, slot: IKeySlot) {
        PlayerKeydownEvent(player, slot).call()
    }

    fun PlayerProfile.cast(skill: Skill, mark: Boolean = true): ExecuteResult {

        val result = if (!mark) {
            val session = Session(adaptPlayer(player), skill)
            session.cast()
            session.closed = true
            ExecuteResult.SUCCESS
        } else if (Counting.hasNext(player, skill)) {
            val session = Session(adaptPlayer(player), skill)
            Counting.reset(player, session)
            takeMana(Coerce.toDouble(session.mpCost.get()))
            session.cast()
            session.closed = true
            ExecuteResult.SUCCESS
        } else {
            ExecuteResult.COOLING
        }

        PlayerCastSkillEvent(player, skill, result).call()
        return result
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        profiles.remove(e.player.uniqueId)
    }

}
