package com.bh.planners.core.kether.game.event

import com.bh.planners.core.effect.Target.Companion.toTarget
import com.bh.planners.core.feature.presskey.Emitter
import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

/**
 * on keypress
 */
class ActionKeyPress(
    val key: ParsedAction<*>, val timeout: ParsedAction<*>, val selector: ParsedAction<*>?, val then: ParsedAction<*>
) : ScriptAction<Void>() {
    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer<Int>(key) { key ->
            frame.runTransfer<Long>(timeout) { timeout ->
                if (selector != null) {
                    frame.execPlayer(selector) {
                        Emitter.registerSubscribers(this, key, timeout * 50).thenAccept {
                            if (!frame.isDone) {
                                process(this, frame)
                            }
                        }
                    }
                } else {
                    Emitter.registerSubscribers(frame.asPlayer() ?: return@runTransfer, key, timeout * 50).thenAccept {
                        if (!frame.isDone) {
                            process(frame.asPlayer()!!, frame)
                        }
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun process(player: Player, frame: ScriptFrame) {
        frame.variables()["entity"] = player.toTarget()
        frame.newFrame(then).run<Any>().thenAccept {
            frame.variables().remove("entity")
        }
    }


}