package com.bh.planners.core.kether.selector

import com.bh.planners.api.common.Plugin
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.skill.effect.Target
import org.bukkit.entity.Player
import org.serverct.ersha.dungeon.DungeonPlus

@Plugin("DungeonPlus")
object Team : Selector {
    override val names: Array<String>
        get() = arrayOf("team", "!team")

    override fun check(name: String, target: Target?, args: String, context: Context, container: Target.Container) {
        val entityTarget = target as? Target.Entity ?: return
        if (entityTarget.livingEntity !is Player) return

        val team = DungeonPlus.teamManager.getTeam(entityTarget.livingEntity) ?: return
        team.getOfflinePlayers().forEach {  }

        if (name.isNon()) {

        } else {

        }

    }
}