package com.bh.planners.core.feature.grid

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.PlannersOption
import com.bh.planners.api.event.*
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.configuration.ConfigNode
import taboolib.module.kether.runKether
import taboolib.module.nms.getName

object BukkitGrid {

    val grids = mutableListOf<Grid>()

    @ConfigNode("options.minecraft-grid")
    var isMinecraftGrid: Boolean = false

    @Awake(LifeCycle.ACTIVE)
    fun initGrids() {
        grids.clear()
        grids += PlannersAPI.keySlots.mapNotNull { Grid.get(it.getGroup(null)) }
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

    fun getActionbarValue(player: Player): String {
        val heldItemSlot = player.inventory.heldItemSlot
        val grid = grids.firstOrNull { it.slot == heldItemSlot } ?: return ""
        return getActionbarValue(player, grid)
    }

    fun getActionbarValue(player: Player, grid: Grid): String {
        val skill = get(player, grid) ?: return ""
        val context = Context.Impl(player.toTarget(), skill.instance)
        return runKether {
            ScriptLoader.createFunctionScript(context, gridActionbarValue) { }
        } ?: "&cError $gridActionbarValue"
    }

    fun updateAll(player: Player) {
        PlannersAPI.keySlots.forEach { key -> update(player, key) }
    }

    fun update(player: Player, slot: IKeySlot) {
        val group = slot.getGroup(player)
        if (group.startsWith("minecraft ")) {
            val minecraftGrid = Grid.get(slot)!!
            update(player, minecraftGrid)
        }
    }

    fun update(player: Player, minecraftGrid: Grid) {
        val skill = get(player, minecraftGrid)
        val itemStack = skill?.toIcon(player)?.build() ?: PlannersOption.gridAirIcon
        player.inventory.setItem(minecraftGrid.slot, itemStack)
        player.updateInventory()
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
        if (!isMinecraftGrid) return
        val heldItemSlot = e.player.inventory.heldItemSlot
        if (grids.firstOrNull { it.slot == heldItemSlot } != null) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInitializeEvent) {
        if (!isMinecraftGrid) return
        updateAll(e.player)
    }

    @SubscribeEvent
    fun e(e: PlayerDropItemEvent) {
        if (!isMinecraftGrid) return
        val player = e.player
        val heldItemSlot = player.inventory.heldItemSlot
        if (grids.firstOrNull { it.slot == heldItemSlot } != null) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: InventoryClickEvent) {
        if (isMinecraftGrid && e.whoClicked == e.inventory.holder && grids.firstOrNull { it.slot == e.slot } != null) {
            e.isCancelled = true
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillBindEvent) {
        if (isMinecraftGrid) {
            update(e.player, e.to)
            update(e.player, e.form ?: return)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillUnbindEvent) {
        if (isMinecraftGrid) {
            update(e.player, e.form)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillUpgradeEvent) {
        if (isMinecraftGrid && e.skill.shortcutKey != null) {
            val grid = Grid.get(e.skill.keySlot!!) ?: return
            update(e.player, grid)
        }
    }

    fun getGridBySlot(slot: Int): Grid? {
        return grids.firstOrNull { it.slot == slot }
    }

    fun getRegisteredGridBySlot(slot: Int): Grid? {
        val minecraftGrid = getGridBySlot(slot)
        if (minecraftGrid == null || PlannersAPI.keySlots.all { Grid.get(it) != minecraftGrid }) {
            return null
        }
        return minecraftGrid
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerItemHeldEvent) {
        if (isMinecraftGrid) {
            val minecraftGrid = getRegisteredGridBySlot(e.newSlot) ?: return
            e.isCancelled = true
            val skill = get(e.player, minecraftGrid) ?: return
            PlannersAPI.cast(e.player, skill).handler(e.player, skill.instance)

        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        if (!isMinecraftGrid) return
        initGrids()
        Bukkit.getOnlinePlayers().forEach(this::updateAll)
    }


}