package com.bh.planners.api.counter

import com.bh.planners.core.pojo.Session
import com.bh.planners.core.pojo.Skill
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import java.util.*

object Counting {

    val map = Collections.synchronizedMap(mutableMapOf<String, MutableSet<Baffle>>())

    fun reset(player: Player, session: Session) {
        getCache(player) += Baffle(session.skill.key, Coerce.toLong(session.cooldown.get()))
    }

    fun hasNext(player: Player, skill: Skill): Boolean {
        return getCache(player).find { it.name == skill.key }?.next ?: true
    }

    fun getCache(player: Player) = map.computeIfAbsent(player.name) { mutableSetOf() }

}