package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.attemptAcceptJob
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import taboolib.common.platform.function.submit
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang

class JobUI(viewer: Player) : IUI(viewer) {

    companion object {

        val root: ConfigurationSection
            get() = UI.config.getConfigurationSection("job")!!

        val title: String
            get() = root.getString("title")!!

        val rows: Int
            get() = root.getInt("rows", 6)

        val slots: List<Int>
            get() = root.getIntegerList("job-slots")

    }

    override fun open() {
        viewer.openMenu<Linked<Router>>(title) {
            rows(rows)
            elements { PlannersAPI.routers }
            slots(JobUI.slots)
            onGenerate { _, element, _, _ ->
                buildItem(element.icon) {
                    name = toLabel(name!!, element).colored()
                    flags += ItemFlag.values()
                }
            }
            root.getKeys(false).filter { it.startsWith("icon-") }.forEach {
                val itemStack = buildItem(root.getItemStack(it)!!) {
                    flags += ItemFlag.values()
                }
                root.getIntegerList("${it}.slots").forEach { slot ->
                    set(slot, itemStack)
                }
            }

            onClick { event, element ->
                event.isCancelled = true
                if (event.clickEvent().click == ClickType.SHIFT_LEFT) {
                    submit(delay = 1, async = false) {
                        viewer.closeInventory()
                    }
                    if (profile.attemptAcceptJob(PlannersAPI.getRouterStartJob(element))) {
                        viewer.sendLang("player-job-selected", element.name)
                    }
                } else {
                    viewer.sendLang("shift-left-info", element.name)
                }
            }

        }
    }

    fun toLabel(string: String, router: Router): String {
        return string.replace("{name}", router.name)
    }

}
