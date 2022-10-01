package com.bh.planners.core.ui

import com.bh.planners.Planners
import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.key.IKeySlot
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.function.info
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.library.xseries.parseToMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta

class ShortcutSelector(viewer: Player, val callback: IKeySlot.() -> Unit) : IUI(viewer) {


    companion object {

        val config: ConfigurationSection
            get() = UI.config.getConfigurationSection("shortcut-selector")!!

        val title: String
            get() = config.getString("title")!!

        val rows: Int
            get() = config.getInt("rows", 6)

        val slots: List<Int>
            get() = config.getIntegerList("key-slots")

        fun toIcon(key: IKeySlot): ItemStack {
            return buildItem(config.getItemStack("key-icon")!!) {
                name = format(key, config.getString("key-icon.name")!!)
                lore.clear()
                lore += config.getStringList("key-icon.lore-pre").map { format(key, it) }
                lore += key.description.map { format(key, it) }
                lore += config.getStringList("key-icon.lore-post").map { format(key, it) }
            }
        }

        fun format(key: IKeySlot, str: String): String {
            return str.replace("{name}", key.name).replace("{key}", key.key).replace("{group}", key.group)
        }
    }

    override fun open() {
        viewer.openMenu<Linked<IKeySlot>>(title) {
            rows(rows)
            slots(ShortcutSelector.slots)
            elements { PlannersAPI.keySlots }

            onGenerate { _, element, _, _ -> toIcon(element) }

            config.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(config.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                config.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }
            onClick { _, element ->
                callback(element)
            }

        }
    }
}