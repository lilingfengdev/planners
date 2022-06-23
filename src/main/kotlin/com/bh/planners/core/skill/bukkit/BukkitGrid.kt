package com.bh.planners.core.skill.bukkit

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PlayerSkillBindEvent
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info

object BukkitGrid {

    val grids = mutableListOf<Grid>()


    @Awake(LifeCycle.ENABLE)
    fun initGrids() {
        grids.clear()
        grids += PlannersAPI.keySlots.mapNotNull { Grid.get(it.group) }
    }

    val gridInteract: String
        get() = PlannersOption.root.getString("grid-interact")!!

    val gridActionbarValue: String
        get() = PlannersOption.root.getString("grid-actionbar")!!

    val Player.isHandGrid: Boolean
        get() = inventory.heldItemSlot in grids.map { it.slot }

    fun toActionbarValue(player: Player): String {
        return gridActionbarValue
    }

    fun updateAll(player: Player) {
        grids.forEach {
            update(player, it)
        }
    }

    //
    fun update(player: Player, grid: Grid) {
        val skillByGrid = get(player, grid)
        if (skillByGrid == null) {
            player.inventory.setItem(grid.slot, PlannersOption.gridAirIcon)
        } else {
            player.inventory.setItem(grid.slot, skillByGrid.toIcon(player, false).build())
        }
    }

    fun getSkillByGrid(profile: PlayerProfile, grid: Grid): PlayerJob.Skill? {
        val skills = profile.getSkills()
        return skills.filter { it.keySlot != null }.firstOrNull { Grid.get(it.keySlot!!) == grid }
    }

    fun get(player: Player, grid: Grid): PlayerJob.Skill? {
        if (player.plannersProfileIsLoaded) {
            val profile = player.plannersProfile
            return getSkillByGrid(profile, grid)
        }
        return null
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        updateAll(e.player)
    }

    @SubscribeEvent
    fun e(e: PlayerDropItemEvent) {
        val player = e.player
        val heldItemSlot = player.inventory.heldItemSlot
        if (grids.firstOrNull { it.slot == heldItemSlot } != null) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: InventoryClickEvent) {
        if (grids.firstOrNull { it.slot == e.slot } != null) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillBindEvent) {
        val grid = Grid.get(e.keySlot) ?: return
        if (grid in grids) {
            update(e.player, grid)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.hasItem() && e.action.name == gridInteract && e.hand == EquipmentSlot.HAND) {
            val player = e.player
            val heldItemSlot = player.inventory.heldItemSlot
            val grid = grids.firstOrNull { it.slot == heldItemSlot } ?: return
            e.isCancelled = true
            val skill = get(player, grid) ?: return
            PlannersAPI.cast(player, skill)

        }
    }


    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        initGrids()
    }

}