package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.plannersProfile
import com.bh.planners.api.event.PlayerSelectedJobEvent
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.player.PlayerProfile
import com.bh.planners.core.storage.Storage
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import taboolib.common.platform.ProxyParticle
import taboolib.common.platform.function.submit
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.getItemStack
import taboolib.module.effect.Circle
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.sendLang
import taboolib.platform.util.toProxyLocation

class JobUI(val player: Player) {

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

    val profile: PlayerProfile
        get() = player.plannersProfile


    fun open() {
        player.openMenu<Linked<Router>>(title) {
            rows(rows)
            elements { PlannersAPI.routers.filter { it.routes.isNotEmpty() } }
            slots(slots)
            onGenerate { _, element, _, _ ->
                buildItem(element.icon) {
                    name = toLabel(name!!, element)
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
                    player.closeInventory()

                    Storage.INSTANCE.createPlayerJob(player, element.routes[0].job).thenAccept {
                        profile.job = it
                        Storage.INSTANCE.updateCurrentJob(profile)
                        PlayerSelectedJobEvent(profile).call()
                        player.sendLang("job-selected", it.instance.option.name)
                    }
                } else {
                    player.sendLang("shift-left-info", element.name)
                }
            }

        }
    }

    fun toLabel(string: String, router: Router): String {
        return string.replace("{name}", router.name)
    }

}