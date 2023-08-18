package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper
import com.bh.planners.core.kether.common.KetherHelper.containerOrSender
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.parseTargetContainer
import org.bukkit.Sound
import taboolib.platform.util.sendActionBar
import java.util.*

@CombinationKetherParser.Used
fun actionbar() = KetherHelper.simpleKetherParser<Unit> {
    it.group(text(), containerOrSender()).apply(it) { text, container ->
        now {
            container.forEachPlayer { sendActionBar(text) }
        }
    }
}

@CombinationKetherParser.Used
fun title() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
            text(),
            command("subtitle", then = text()).option(),
            command("by", "with", then = int().and(int(), int())).option().defaultsTo(Triple(0, 20, 0)),
            containerOrSender()
    ).apply(it) { title, subtitle, with, container ->
        now {
            container.forEachPlayer {
                sendTitle(title, subtitle ?: "", with.first, with.second, with.third)
            }
        }
    }
}

@CombinationKetherParser.Used
fun tell() = KetherHelper.simpleKetherParser<Unit>("send","message") {
    it.group(text(),containerOrSender()).apply(it) { message,container ->
        now { container.forEachPlayer { sendMessage(message) } }
    }
}

@CombinationKetherParser.Used
fun teleport() = KetherHelper.simpleKetherParser<Unit>("tp") {
    it.group(any(),containerOrSender()).apply(it) { loc, container ->
        now {
            val location = parseTargetContainer(loc!!, getContext()).firstBukkitLocation()!!
            container.forEachPlayer { teleport(location) }
        }
    }
}

@CombinationKetherParser.Used
fun sound() = KetherHelper.simpleKetherParser<Unit> {
    it.group(
        text(),
        command("by", "with", then = float().and(float())).option().defaultsTo(1f to 1f),
        containerOrSender()
    ).apply(it) { sound, with, container ->
        now {
            val (volume, pitch) = with
            container.forEachPlayer {
                if (sound.startsWith("resource:")) {
                    playSound(location, sound.substring("resource:".length), volume, pitch)
                } else {
                    playSound(location, Sound.valueOf(sound.replace('.', '_').uppercase(Locale.getDefault())), volume, pitch)
                }
            }
        }
    }
}