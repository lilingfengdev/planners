package com.bh.planners.api.compat

import com.bh.planners.api.Counting
import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.PlannersAPI.plannersProfileIsLoaded
import com.bh.planners.api.compat.GermPluginHook.isGermSlot
import com.bh.planners.api.compat.GermPluginHook.skip
import com.bh.planners.api.event.PlayerCastSkillEvent
import com.bh.planners.api.event.PlayerInitializeEvent
import com.bh.planners.api.event.PlayerSkillBindEvent
import com.bh.planners.api.event.PlayerSkillUpgradeEvent
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.ui.SkillIcon.Companion.buildIconItem
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import com.germ.germplugin.api.GermPacketAPI
import com.germ.germplugin.api.GermSlotAPI
import com.germ.germplugin.api.event.GermClientLinkedEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.OptionalEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.library.xseries.XMaterial

object GermPluginHook {

    private val isEnable by lazy { Bukkit.getPluginManager().isPluginEnabled("GermPlugin") }

    @SubscribeEvent(bind = "com.germ.germplugin.api.event.GermClientLinkedEvent")
    fun e0(ope: OptionalEvent) {
        val e = ope.get<GermClientLinkedEvent>()
        if (e.player.plannersProfileIsLoaded) {
            updateAll(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInitializeEvent) {
        if (isEnable) {
            updateAll(e.player)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillUpgradeEvent) {
        if (e.skill.keySlot?.isGermSlot == true) {
            update(e.player, e.skill.keySlot!!.skip, e.skill.buildIconItem(e.player))
            refresh(e.player, e.skill)
        }
    }

    @SubscribeEvent(ignoreCancelled = true, priority = EventPriority.MONITOR)
    fun e(e: PlayerCastSkillEvent.Record) {
        refresh(e.player, e.skill)
    }

    fun refresh(player: Player, skill: Skill) {
        val plannersProfile = player.plannersProfile
        refresh(player, plannersProfile.getSkill(skill.key) ?: return)
    }

    fun refresh(player: Player, skill: PlayerJob.Skill) {
        if (skill.shortcutKey != null && skill.keySlot?.isGermSlot == true) {
            val itemStack = GermSlotAPI.getItemStackFromIdentity(player, skill.keySlot!!.skip)
            val tick = (Counting.getCountdown(player, skill.instance) / 50).toInt()
            GermPacketAPI.setItemStackCooldown(player, itemStack, tick)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerSkillBindEvent) {
        if (isEnable) {
            updateAll(e.player)
        }
    }

    private val IKeySlot.isGermSlot: Boolean
        get() = group.startsWith("germ_plugin")

    private val IKeySlot.skip: String
        get() = group.replaceFirst("germ_plugin", "").trim()

    fun updateAll(player: Player) {
        val profile = player.plannersProfile
        PlannersAPI.keySlots.filter { it.isGermSlot }.forEach {
            val skill = profile.getSkill(it)
            if (skill == null) {
                update(player, it.skip, XMaterial.AIR.parseItem()!!)
            } else {
                update(player, it.skip, skill.toIcon(player).build())
            }
        }
    }

    fun update(player: Player, id: String, item: ItemStack) {
        GermSlotAPI.saveItemStackToDatabase(player, id, item)
    }


}