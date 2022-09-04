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

    private val teams = HashMap<ChatColor, Team>()
    private val entityCache = HashMap<String, ChatColor>()

    private fun initTeams() {
        ChatColor.values().forEach {
            val mainScoreBoard = Bukkit.getScoreboardManager()?.mainScoreboard!!
            val team = mainScoreBoard.getTeam("Planners_Glow_$it") ?: mainScoreBoard.registerNewTeam("Planners_Glow_$it")
            team.color = it
            team.prefix = it.toString()
            teams[it] = team
        }
    }

    @Awake(LifeCycle.ACTIVE)
    fun init() {
        initTeams()
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