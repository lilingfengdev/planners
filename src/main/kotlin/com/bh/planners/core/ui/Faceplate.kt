package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.addPoint
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.api.next
import com.bh.planners.core.kether.LazyGetter
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.Skill
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.storage.Storage
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.adaptPlayer
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyMeta
import taboolib.platform.util.sendLang

class Faceplate(viewer: Player, val skill: Skill) : IUI(viewer) {

    constructor(viewer: Player, skillName: String) : this(viewer, PlannersAPI.getSkill(skillName)!!)

    constructor(viewer: Player, skill: PlayerJob.Skill) : this(viewer, skill.instance)

    companion object {

        val config: ConfigurationSection
            get() = UI.config.getConfigurationSection("faceplate")!!

        val title: String
            get() = config.getString("title")!!

        val rows: Int
            get() = config.getInt("rows", 6)

        val slots: List<Int>
            get() = config.getIntegerList("item-slots")

        val itemSlot: Int
            get() = config.getInt("item-slot")

        val nextIconSlot: Int
            get() = config.getInt("next-icon.slot")


        val nextIcon: ItemStack
            get() = config.getItemStack("next-icon")!!

        fun nextUpgradePoint(player: Player, skill: PlayerJob.Skill): Int {
            val session = Session(adaptPlayer(player), skill)
            return Coerce.toInteger(session.upgradePoint.get())
        }


    }

    val playerSkill: PlayerJob.Skill
        get() = profile.getSkill(skill.key)!!

    class Session(executor: ProxyCommandSender, override val playerSkill: PlayerJob.Skill) :
        Context(executor, playerSkill.instance) {

        val upgradePoint = variables["upgradePoint"] ?: LazyGetter { 0 }

    }

    fun toNextIcon(): ItemStack {
        return buildItem(nextIcon) {
            name = format(name!!)
            lore.forEachIndexed { index, s ->
                lore[index] = format(s)
            }
        }

    }

    val nextUpgradePoint = nextUpgradePoint(viewer, playerSkill)

    override fun open() {
        viewer.openMenu<Linked<SkillIcon>>(title) {
            rows(rows)
            elements { (1..skill.option.levelCap).map { SkillIcon(viewer, skill.key, it, false) } }
            slots(slots)
            config.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(config.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                config.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }
            set(itemSlot, profile.getSkill(skill.key)!!.toIcon(viewer, false).build())
            set(nextIconSlot, toNextIcon()) {
                profile.addPoint(nextUpgradePoint)
                profile.next(playerSkill)
                viewer.sendLang("skill-upgrade-success", skill.option.name, playerSkill.level)
                open()
            }
            onGenerate { _, element, index, _ ->
                when {
                    index < playerSkill.level -> {
                        element.build().modifyMeta<ItemMeta> {
                            addEnchant(Enchantment.DAMAGE_ALL, 1, false)
                            addItemFlags(*ItemFlag.values())
                        }
                    }
                    else -> element.build()
                }
            }


            onClick { event, element ->

            }

        }
    }

    private fun format(str: String): String {
        return str.replace("{point}", nextUpgradePoint.toString())
    }

}