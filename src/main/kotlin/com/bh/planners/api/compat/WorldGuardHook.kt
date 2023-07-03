package com.bh.planners.api.compat

import com.bh.planners.api.PlannersOption
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.DefaultFlag
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object WorldGuardHook {

    private val worldGuard = Bukkit.getPluginManager().isPluginEnabled("WorldGuard")

    lateinit var worldGuardPlugin: WorldGuardPlugin

    val skill = PlannersOption.root.getString("WorldGuard.castSkill") ?: "禁止释放"
    val cast = PlannersOption.root.getBoolean("WorldGuard.skill")
    val enable = PlannersOption.root.getBoolean("WorldGuard.enable")

    fun cast(player: Player) : Boolean {
        if (!worldGuard) return true
        if (enable) {
            val localPlayer = worldGuardPlugin.wrapPlayer(player)
            val query = worldGuardPlugin.regionContainer.createQuery()
            if (!query.testState(player.location, localPlayer, DefaultFlag.PVP)) {
                return false
            }
        }
        return true
    }



}