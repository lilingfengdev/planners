package com.bh.planners.core.ui

import com.bh.planners.api.bind
import com.bh.planners.core.pojo.player.PlayerJob
import com.bh.planners.core.ui.SkillIcon.Companion.toIcon
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.modifyLore
import taboolib.platform.util.sendLang

class Backpack(viewer: Player) : IUI(viewer) {

    companion object {

        val config: ConfigurationSection
            get() = UI.config.getConfigurationSection("skill-backpack")!!

        val title: String
            get() = config.getString("title")!!

        val rows: Int
            get() = config.getInt("rows", 6)

        val slots: List<Int>
            get() = config.getIntegerList("skill-slots")

        val addonInfo: List<String>
            get() = config.getStringList("addon-info")

    }

    override fun open() {
        viewer.openMenu<Linked<PlayerJob.Skill>>(title) {
            rows(rows)
            slots(Backpack.slots)
            elements { profile.getSkills() }

            onGenerate { _, element, _, _ ->
                element.toIcon(viewer).run {
                    build().modifyLore {
                        addAll(addonInfo.map { format(it) })
                    }
                }
            }

            config.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(config.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                config.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }
            onClick { event, element ->
                if (event.clickEvent().isLeftClick) {
                    Faceplate(viewer, element).open()
                }
                if (event.clickEvent().isRightClick) {

                    if (element.level == 0) {
                        viewer.sendLang("skill-zero", element.name)
                        return@onClick
                    }

                    if (!element.instance.option.isBind) {
                        viewer.sendLang("skill-not-bind", element.name)
                        return@onClick
                    }

                    ShortcutSelector(viewer) {
                        // 取消绑定
                        if (element.shortcutKey == this.key) {
                            viewer.sendLang("skill-un-bind-shortcut", element.instance.option.name)
                        } else {
                            viewer.sendLang("skill-bind-shortcut", element.instance.option.name, name)
                        }
                        profile.bind(element, this)
                        open()
                    }.open()

                }
            }


        }
    }

}