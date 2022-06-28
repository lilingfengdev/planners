package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import com.bh.planners.core.kether.selectorAction
import org.bukkit.Sound
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestContext
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionSound(val sound: String, val volume: Float, val pitch: Float, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    override fun run(frame: QuestContext.Frame): CompletableFuture<Void> {

        if (selector != null) {
            frame.createTargets(selector).thenAccept {
                it.forEachPlayer { execute(this, sound, volume, pitch) }
            }
        } else {
            val viewer = frame.script().sender?.castSafely<Player>() ?: error("No player selected.")
            execute(viewer, sound, volume, pitch)
        }

        return CompletableFuture.completedFuture(null)
    }

    fun execute(player: Player, sound: String, volume: Float, pitch: Float) {
        if (sound.startsWith("resource:")) {
            player.playSound(player.location, sound.substring("resource:".length), volume, pitch)
        } else {
            kotlin.runCatching {
                player.playSound(player.location, Sound.valueOf(sound.replace('.', '_').uppercase()), volume, pitch)
            }
        }
    }

    internal object Parser {

        /**
         * sound block_stone_break by 1 1 <the selector>
         */
        @KetherParser(["sound"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val sound = it.nextToken()
            var volume = 1.0f
            var pitch = 1.0f
            it.mark()
            try {
                it.expects("by", "with")
                volume = it.nextDouble().toFloat()
                pitch = it.nextDouble().toFloat()
            } catch (ignored: Exception) {
                it.reset()
            }
            ActionSound(sound, volume, pitch, it.selectorAction())
        }
    }
}