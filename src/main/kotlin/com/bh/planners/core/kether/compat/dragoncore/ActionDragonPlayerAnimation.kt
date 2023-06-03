package com.bh.planners.core.kether.compat.dragoncore


import com.bh.planners.core.kether.execPlayer
import eos.moe.dragoncore.network.PacketSender
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonPlayerAnimation (
    val state: String,
    val remove: Boolean,
    val selector: ParsedAction<*>
) : ScriptAction<Void>() {

    fun execute(p: Player, state: String, remove: Boolean) {
        if (remove) {
            PacketSender.removePlayerAnimation(p)
        } else {
            PacketSender.setPlayerAnimation(p,state)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.execPlayer(selector) { execute(this, state, remove) }
        return CompletableFuture.completedFuture(null)
    }
}