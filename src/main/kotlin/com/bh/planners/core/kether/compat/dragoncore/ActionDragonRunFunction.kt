package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.core.kether.containerOrSender
import eos.moe.dragoncore.network.PacketSender
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionDragonRunFunction(
    val ui: ParsedAction<*>,
    val function: String,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(ui).str { ui ->
            frame.containerOrSender(selector).thenAccept {
                it.forEachPlayer { PacketSender.sendRunFunction(player, ui, function, false) }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}