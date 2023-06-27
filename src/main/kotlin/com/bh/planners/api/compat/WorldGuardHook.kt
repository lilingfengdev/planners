package com.bh.planners.api.compat

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.ManaCounter.addMana
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.common.ExecuteResult
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.DefaultFlag
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object WorldGuardHook {

    fun cast(player: Player) : ExecuteResult? {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            if (PlannersOption.root.getBoolean("WorldGuard.enable")) {
                val localPlayer = WorldGuardPlugin().wrapPlayer(player)
                val query = WorldGuardPlugin().regionContainer.createQuery()
                val skill = PlannersOption.root.getString("WorldGuard.castSkill") ?: ""
                if (!query.testState(player.location, localPlayer, DefaultFlag.PVP)) {
                    if (PlannersOption.root.getBoolean("WorldGuard.skill")) {
                        ContextAPI.create(player, skill, 1)?.cast()
                    }
                    return ExecuteResult.WorldGuardPVP
                }
            }
        }
        return null
    }



}