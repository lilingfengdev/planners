package com.bh.planners.core.feature.grid

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerSkillBindEvent
import com.bh.planners.api.event.PlayerSkillUpgradeEvent
import com.bh.planners.api.event.PluginReloadEvent
import com.bh.planners.core.kether.namespaces
import com.bh.planners.core.kether.rootVariables
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.kether.KetherFunction
import taboolib.module.kether.printKetherErrorMessage

object BukkitGrid {

    val grids = mutableListOf<Grid>()


    @Awake(LifeCycle.ACTIVE)
    fun initGrids() {
        grids.clear()
        grids += PlannersAPI.keySlots.mapNotNull { Grid.get(it.group) }
    }

    val gridInteractActions: List<String>
        get() = PlannersOption.root.getStringList("grid-interact-actions")

    val gridActionbarValue: String
        get() = PlannersOption.root.getString("grid-actionbar")!!

    val Player.isHandGrid: Boolean
        get() = inventory.heldItemSlot in grids.map { it.slot }

    val Player.handGrid: Grid?
        get() = grids.firstOrNull { it.slot == inventory.heldItemSlot }

    val Player.handSkill: PlayerJob.Skill?
        get() = get(this, handGrid)

    fun toActionbarValue(player: Player): String {
        val heldItemSlot = player.inventory.heldItemSlot
        val grid = grids.firstOrNull { it.slot == heldItemSlot } ?: return ""
        return toActionbarValue(player, grid)
    }

    fun toActionbarValue(player: Player, grid: Grid): String {
        val skill = get(player, grid) ?: return ""
        return try {
            KetherFunction.parse(gridActionbarValue, sender = adaptPlayer(player), namespace = namespaces) {
                rootFrame().rootVariables()["@Skill"] = skill
                rootFrame().rootVariables()["@Context"] = Context.Impl(sender!!, skill.instance)
            }
        } catch (e: Throwable) {
            e.printKetherErrorMessage()
            "&cError $gridActionbarValue"
        }
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

    fun get(player: Player, grid: Grid?): PlayerJob.Skill? {
        if (grid == null) return null
        if (player.plannersProfileIsLoaded) {
            val profile = player.plannersProfile
            return getSkillByGrid(profile, grid)
        }
        return null
    }

    @SubscribeEvent
    fun e(e: PlayerSwapHandItemsEvent) {
        val heldItemSlot = e.player.inventory.heldItemSlot
        if (grids.firstOrNull { it.slot == heldItemSlot } != null) {
            e.isCancelled = true
        }

    }

    @SubscribeEvent
    fun e(e: PlayerInitializeEvent) {
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
        updateAll(e.player)
    }

    @SubscribeEvent
    fun e(e: PlayerSkillUpgradeEvent) {
        if (e.skill.shortcutKey != null) {
            val grid = Grid.get(e.skill.keySlot!!) ?: return
            update(e.player, grid)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEvent) {
        if (e.hasItem() && e.action.name in gridInteractActions && e.hand == EquipmentSlot.HAND) {
            val player = e.player
            val heldItemSlot = player.inventory.heldItemSlot
            val grid = grids.firstOrNull { it.slot == heldItemSlot } ?: return
            e.isCancelled = true
            val skill = get(player, grid) ?: return
            PlannersAPI.cast(player, skill).handler(e.player, skill.instance)

        }
    }


    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        initGrids()
        Bukkit.getOnlinePlayers().forEach(this::updateAll)
    }


}