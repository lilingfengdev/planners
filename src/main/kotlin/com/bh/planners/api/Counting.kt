package com.bh.planners.api

import com.bh.planners.api.common.Baffle
import com.bh.planners.api.event.PlayerSkillCooldownEvent
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import java.util.*

object Counting {

    val map = Collections.synchronizedMap(mutableMapOf<String, MutableSet<Baffle>>())

    fun reset(player: Player, session: Session) {
        val get = session.cooldown.get()
        set(player, session.skill, Coerce.toLong(get) * 50)
    }

    fun hasNext(player: Player, skill: Skill): Boolean {
        return getCache(player).find { it.name == skill.key }?.next ?: true
    }

    fun getCountdown(player: Player, skill: Skill): Long {
        return getCache(player).find { it.name == skill.key }?.countdown ?: 0
    }

    fun increase(player: Player, skill: Skill, amount: Long) {
        val baffle = getCache(player).firstOrNull { it.name == skill.key } ?: return
        val event = PlayerSkillCooldownEvent.Increase.Pre(player, skill, amount)
        if (event.call()) {
            baffle.increase(event.amount)
            PlayerSkillCooldownEvent.Increase.Post(player, skill, amount).call()
        }
    }

    fun set(player: Player, skill: Skill, amount: Long) {
        val baffles = getCache(player)
        baffles.removeIf { it.next || it.name == skill.key }
        val event = PlayerSkillCooldownEvent.Set(player, skill, amount)
        if (event.call()) {
            baffles += Baffle(skill.key, event.amount)
        }
    }

    fun reduce(player: Player, skill: Skill, amount: Long) {
        val baffle = getCache(player).firstOrNull { it.name == skill.key } ?: return
        val event = PlayerSkillCooldownEvent.Reduce.Pre(player, skill, amount)
        if (event.call()) {
            baffle.reduce(event.amount)
            PlayerSkillCooldownEvent.Reduce.Post(player, skill, amount).call()
        }
    }


    fun getCache(player: Player): MutableSet<Baffle> = map.computeIfAbsent(player.name) { mutableSetOf() }

}