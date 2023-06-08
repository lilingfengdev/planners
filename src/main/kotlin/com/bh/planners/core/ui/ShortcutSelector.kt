package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.core.pojo.key.IKeySlot
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem

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
            return toIcon(null, key)
        }

        fun toIcon(player: Player?, temp: ItemStack, key: IKeySlot): ItemStack {
            return buildItem(temp) {
                name = format(player, key, config.getString("key-icon.name")!!)
                lore.clear()
                lore += config.getStringList("key-icon.lore-pre")
                lore += key.description
                lore += config.getStringList("key-icon.lore-post")

                lore.forEachIndexed { index, s ->
                    lore[index] = format(player, key, s)
                }
            }
        }

        fun toIcon(player: Player?, key: IKeySlot): ItemStack {
            return toIcon(player, config.getItemStack("key-icon")!!, key)
        }

        fun format(player: Player?, key: IKeySlot, str: String): String {
            return str.replace("{name}", key.name).replace("{key}", key.key).replace("{group}", key.getGroup(player))
        }
    }

    override fun open() {
        viewer.openMenu<Linked<IKeySlot>>(title) {
            rows(rows)
            slots(ShortcutSelector.slots)
            elements { PlannersAPI.keySlots }

            onGenerate { _, element, _, _ -> toIcon(viewer, element) }

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