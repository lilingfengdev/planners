package com.bh.planners.api.compat

import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PluginReloadEvent
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.DefaultFlag
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent

object WorldGuardHook {

    private lateinit var worldGuardPlugin: WorldGuardPlugin

    val skill = PlannersOption.root.getString("WorldGuard.castSkill") ?: "禁止释放"
    val cast = PlannersOption.root.getBoolean("WorldGuard.skill")

    val enable: Boolean
        get() = Bukkit.getPluginManager().isPluginEnabled("WorldGuard")

    @Awake(LifeCycle.ACTIVE)
    fun active() {
        if (enable) {
            try {
                worldGuardPlugin = WorldGuardPlugin.inst()
            } catch (_: Throwable) {
                error("缺少wg，请不要开启config中的worldGuard功能")
            }
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        if (enable) {
            worldGuardPlugin = WorldGuardPlugin.inst()
        }
    }

    fun cast(player: Player) : Boolean {
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