package com.bh.planners.api

import com.bh.planners.Planners
import com.bh.planners.core.pojo.Router
import org.bukkit.inventory.ItemStack
import taboolib.library.configuration.ConfigurationSection
import taboolib.library.xseries.XMaterial
import taboolib.library.xseries.getItemStack
import taboolib.module.configuration.Configuration

object PlannersOption {

    val root: ConfigurationSection
        get() = Planners.config.getConfigurationSection("options")!!

    val scopeThreshold: List<Double>
        get() = root.getDoubleList("scope-threshold")

    val autoSaveFlagPeriod: Long
        get() = root.getLong("autoSaveFlagPeriod", 6000)

    val infos: List<String>
        get() = root.getStringList("infos")

    val gridAirIcon: ItemStack
        get() = root.getItemStack("grid-air") ?: XMaterial.STONE.parseItem()!!

    val regainManaPeriod: Long
        get() = root.getLong("regain-mana-period", 20L)

    val regainManaExperience: String
        get() = root.getString("regain-mana-eval")!!

    val upgradePoints: String?
        get() = root.getString("upgrade-points")
}
