package com.bh.planners.api

import com.bh.planners.Planners
import org.bukkit.inventory.ItemStack
import taboolib.common.util.asList
import taboolib.common5.cdouble
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.ConfigNodeTransfer

object PlannersOption {

    val root: ConfigurationSection
        get() = Planners.config.getConfigurationSection("options")!!

    @ConfigNode("options.scope-threshold")
    val scopeThreshold = ConfigNodeTransfer<Any, List<Double>> {
        asList().map { it.cdouble }
    }

    @ConfigNode("options.auto-save-flag-period")
    val autoSaveFlagPeriod = 6000L

    @ConfigNode("options.infos")
    val infos = ConfigNodeTransfer<Any, List<String>> {
        asList()
    }

    @ConfigNode("options.grid-air")
    val gridAirIcon = ConfigNodeTransfer<ConfigurationSection, ItemStack> {
        XItemStack.deserialize(this)
    }

    @ConfigNode("options.regain-mana-period")
    val regainManaPeriod = 20L

    @ConfigNode("options.regain-mana-eval")
    val regainManaExperience = "100"

    @ConfigNode("options.upgrade-points")
    val upgradePoints: String? = null
}
