package com.bh.planners.api.compat

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PlayerCastSkillEvents
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.DefaultFlag
import org.bukkit.Bukkit
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.colored

object WorldGuardHook {

    @SubscribeEvent
    fun cast(e: PlayerCastSkillEvents.Pre) {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            if (PlannersOption.root.getBoolean("WorldGuard.enable")) {
                val player = e.player
                val query = WorldGuardPlugin().regionContainer.createQuery()
                val title1 = PlannersOption.root.getString("WorldGuard.title1") ?: "&4禁止释放"
                val title2 = PlannersOption.root.getString("WorldGuard.title2") ?: "&b当前区域禁止释放技能"
                val skill = PlannersOption.root.getString("WorldGuard.castSkill") ?: "禁止释放技能"
                if (!query.testState(player.location, player, DefaultFlag.PVP)) {
                    e.isCancelled = true
                    if (PlannersOption.root.getBoolean("WorldGuard.title")) {
                        player.sendTitle(title1.colored(), title2.colored(), 10, 10, 10)
                    }
                    if (PlannersOption.root.getBoolean("WorldGuard.skill")) {
                        ContextAPI.create(player, skill, 1)?.cast()
                    }
                }
            }
        }
    }



}