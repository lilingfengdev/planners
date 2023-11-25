package com.bh.planners.core.effect.custom

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.common.EffectSpawner
import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptPlayer
import taboolib.module.effect.wing.WingRender

object EffectWing {
    val name: String
        get() = "wing"

    fun render(option: EffectOption, spawner: EffectSpawner, player: Player) {

        val height = option.height.toInt()
        val width = option.width.toInt()

        val renderer = WingRender(height, width)

        renderer.add(" a   ")
        renderer.add(" aa  ")
        renderer.add(" aaa  ")
        renderer.add(" aaaa ")
        renderer.add(" aaaaa")
        renderer.add("  aaaaaa")
        renderer.add("  aaaaaa")
        renderer.add(" aaaaa")
        renderer.add(" aaaa ")
        renderer.add(" aaa  ")
        renderer.add(" aa  ")
        renderer.add(" a   ")
        renderer.set('a') {
            spawner.spawn(it)
        }

        repeat(10) {
            renderer.render(adaptPlayer(player), it, 0.0, 0.5, 0.0)
        }

    }

}