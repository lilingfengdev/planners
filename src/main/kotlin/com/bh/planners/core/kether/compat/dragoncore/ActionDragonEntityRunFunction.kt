package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.createContainer
import eos.moe.dragoncore.network.PacketSender
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonEntityRunFunction(
        val function: String,
        val entity: ParsedAction<*>,
        val selector: ParsedAction<*>?,
    ) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.createContainer(entity).thenAccept { container ->
            frame.containerOrSender(selector).thenAccept {
                it.forEachPlayer {
                    val player = this
                    container.forEachEntity {
                        PacketSender.runEntityAnimationFunction(player, this.uniqueId, function)
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}