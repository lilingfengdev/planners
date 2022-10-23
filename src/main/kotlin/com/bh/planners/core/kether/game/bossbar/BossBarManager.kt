package com.bh.planners.core.kether.game.bossbar

import com.bh.planners.api.common.SimpleTimeoutTask
import java.util.Collections

object BossBarManager {

    private val bars = Collections.synchronizedList(mutableListOf<BossBar>())

    fun createBossbar(id: String): BossBar {
        val bossBar = BossBar(id)
        bars += bossBar
        return bossBar
    }

    fun createBossbar(id: String, tick: Long): BossBar {
        val bossbar = createBossbar(id)
        // 注销
        if (tick != -1L) {
            SimpleTimeoutTask.createSimpleTask(tick) { removeBossbar(bossbar) }
        }

        return bossbar
    }

    fun getBossbar(id: String) = bars.firstOrNull { it.id == id }

    fun removeBossbar(id: String) {
        removeBossbar(getBossbar(id) ?: return)
    }

    fun removeBossbar(bossBar: BossBar) {
        bars -= bossBar
        bossBar.visible = false
        bossBar.clearViewers()
    }

}