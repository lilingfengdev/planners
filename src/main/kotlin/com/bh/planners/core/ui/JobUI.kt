package com.bh.planners.core.ui

import com.bh.planners.api.PlannersAPI
import com.bh.planners.api.PlannersAPI.profile
import com.bh.planners.api.particle.ParticleImpl
import com.bh.planners.core.pojo.Router
import com.bh.planners.core.pojo.player.PlayerJob
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


                if (event.clickEvent().click == ClickType.SHIFT_LEFT) {
                    player.closeInventory()

                    Storage.INSTANCE.createPlayerJob(player, element.routes[0].job).thenAccept {
                        player.profile().job = it
                        player.sendLang("job-selected", it.instance.option.name)
                    }


                    submit(async = true) {
                        (0 until 5).forEach { index ->
                            Thread.sleep(50)
                            Circle(
                                player.location.toProxyLocation(), 1.0 * (index + 1),
                                ParticleImpl().apply {
                                    add(ParticleImpl.Key.PLAYER, player)
                                    add(ParticleImpl.Key.PARTICLE, ProxyParticle.CLOUD)
                                },
                            ).show()
                        }
                    }

                    submit(async = true, delay = 5) {
                        Circle(
                            player.location.toProxyLocation(),
                            1.5,
                            ParticleImpl().apply {
                                add(ParticleImpl.Key.PLAYER, player)
                                add(ParticleImpl.Key.PARTICLE, ProxyParticle.CLOUD)
                                add(ParticleImpl.Key.COUNT, 1)
                                add(ParticleImpl.Key.SPEED, 0.0)
                                add(ParticleImpl.Key.OFFSET, arrayOf(0.0, 2.0, 0.0))
                            },
                        ).show()
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
