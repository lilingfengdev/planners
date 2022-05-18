package com.bh.planners.core.pojo.level

import com.bh.planners.Planners
import com.bh.planners.api.event.PluginReloadEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.SubscribeEvent

/**
 * Chemdah
 * ink.ptms.chemdah.module.level.LevelSystem
 *
 * @author sky
 * @since 2021/3/8 11:13 下午
 */
object LevelSystem {


    val levels = HashMap<String, LevelOption>()

    fun getLevelOption(name: String): LevelOption? {
        return levels[name]
    }

    @Awake(LifeCycle.ENABLE)
    fun load() {
        levels.clear()
        Planners.config.getConfigurationSection("level")?.getKeys(false)?.forEach { node ->
            val section = Planners.config.getConfigurationSection("level.$node")!!
            val algorithm = AlgorithmKether(section)
            levels[node] = LevelOption(algorithm, section.getInt("min"), section)
        }
    }

    @SubscribeEvent
    fun e(e: PluginReloadEvent) {
        this.load()
    }
}
