package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.runTransfer0
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

        frame.runTransfer0<String>(name) { name ->
            frame.runTransfer0<Float>(volume) { volume ->
                frame.runTransfer0<Float>(pitch) { pitch ->
                    frame.runTransfer0<Boolean>(loop) { loop ->
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