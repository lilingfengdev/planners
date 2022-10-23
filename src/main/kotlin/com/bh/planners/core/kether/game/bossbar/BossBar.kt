package com.bh.planners.core.kether.game.bossbar

import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

class BossBar(val id: String) {

    var instance: BossBar? = null

    var title = ""

    var style = BarStyle.SOLID

    var color = BarColor.WHITE

    private val flags = mutableListOf<BarFlag>()

    private val viewers = mutableListOf<Player>()

    var visible: Boolean
        get() = instance?.isVisible ?: false
        set(value) {
            instance?.isVisible = value
        }

    fun create() {
        instance = Bukkit.createBossBar(title, color, style, *flags.toTypedArray())
        viewers.forEach {
            instance!!.addPlayer(it)
        }
    }

    fun update() {
        instance?.setTitle(title)
        instance?.style = style
        instance?.color = color
    }

    fun clearViewers() {
        viewers.clear()
        instance?.removeAll()
    }

    fun addViewer(player: Player) {
        viewers += player
        instance?.addPlayer(player)
    }

    fun addFlag(flag: BarFlag) {
        flags += flag
        instance?.addFlag(flag)
    }

    fun removeFlag(flag: BarFlag) {
        flags -= flag
        instance?.removeFlag(flag)
    }

    fun removeViewer(player: Player) {
        viewers -= player
        instance?.removePlayer(player)
    }

}