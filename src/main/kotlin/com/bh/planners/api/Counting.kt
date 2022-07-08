package com.bh.planners.api

import com.bh.planners.api.common.Baffle
import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import java.util.*

object Counting {

    val map = Collections.synchronizedMap(mutableMapOf<String, MutableSet<Baffle>>())

    fun reset(player: Player, session: Session) {
        val get = session.cooldown.get()
        set(player, session.skill, Coerce.toLong(get) * 1000)
    }

    fun hasNext(player: Player, skill: Skill): Boolean {
        return getCache(player).find { it.name == skill.key }?.next ?: true
    }

    fun getCountdown(player: Player, skill: Skill): Long {
        return getCache(player).find { it.name == skill.key }?.countdown ?: 0
    }

    fun increase(player: Player, skill: Skill, amount: Long) {
        val baffle = getCache(player).firstOrNull { it.name == skill.key } ?: return
        baffle.increase(amount)
    }

    fun set(player: Player, skill: Skill, amount: Long) {
        val baffles = getCache(player)
        baffles.removeIf { it.next }
        baffles += Baffle(skill.key, amount)
    }

    fun reduce(player: Player, skill: Skill, amount: Long) {
        val baffle = getCache(player).firstOrNull { it.name == skill.key } ?: return
        baffle.reduce(amount)
    }

    fun getCache(player: Player) = map.computeIfAbsent(player.name) { mutableSetOf() }

}