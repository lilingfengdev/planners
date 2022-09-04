package com.bh.planners.core.kether.util

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info

object GlowUtil {

    private val teams = ChatColor.values().map { it to it.scoreboard }.toMap()
    private val entityCache = HashMap<String, ChatColor>()

    val mainScoreboard by lazy { Bukkit.getScoreboardManager()?.mainScoreboard!! }

    val ChatColor.team : String
        get() = "Planners_Glow_$name"

    val ChatColor.scoreboard: Team
        get() = mainScoreboard.getTeam(team) ?: mainScoreboard.registerNewTeam(team).also {
            it.color = this
            it.prefix = this.name
        }


    @JvmStatic
    fun setColor(entity: Entity, color: ChatColor) {
        val team = teams[color]!!
        val entry = if (entity is Player) entity.name else entity.uniqueId.toString()
        team.addEntry(entry)
        entityCache[entity.uniqueId.toString()] = color
        entity.isGlowing = true
    }

    @JvmStatic
    fun removeColor(entity: Entity) {
        val entry = if (entity is Player) entity.name else entity.uniqueId.toString()
        if (entityCache.containsKey(entry)) {
            teams[entityCache[entry]]!!.removeEntry(entry)
            entityCache.remove(entry)
        }
        entity.isGlowing = false
    }
}