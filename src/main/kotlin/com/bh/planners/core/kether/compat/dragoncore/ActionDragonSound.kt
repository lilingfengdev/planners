package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.network.PacketSender
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonSound(
    val name: ParsedAction<*>,
    val volume: ParsedAction<*>,
    val pitch: ParsedAction<*>,
    val loop: ParsedAction<*>,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {


    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.readAccept<String>(name) { name ->
            frame.readAccept<Float>(volume) { volume ->
                frame.readAccept<Float>(pitch) { pitch ->
                    frame.readAccept<Boolean>(loop) { loop ->
                        frame.execPlayer(selector) {
                            PacketSender.sendPlaySound(this, name, volume, pitch, loop, 0f, 0f, 0f)
                        }
                    }

                }
            }
        }

        return CompletableFuture.completedFuture(null)
    }
}