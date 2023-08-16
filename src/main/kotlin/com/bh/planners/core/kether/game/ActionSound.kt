package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.catchRunning
import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import org.bukkit.Sound
import org.bukkit.entity.Player
import taboolib.module.kether.*
import java.util.*


@CombinationKetherParser.Used
fun sound() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
        text(),
        command("by", "with", then = float().and(float())).option().defaultsTo(1f to 1f),
        containerOrSender()
    ).apply(it) { sound, with, container ->
        now {
            container.forEachPlayer { execute(this, sound, with.first, with.second) }
        }
    }
}

private fun execute(player: Player, sound: String, volume: Float, pitch: Float) {
    if (sound.startsWith("resource:")) {
        player.playSound(player.location, sound.substring("resource:".length), volume, pitch)
    } else {
        catchRunning {
            player.playSound(player.location, Sound.valueOf(sound.replace('.', '_').uppercase(Locale.getDefault())), volume, pitch)
        }
    }
}