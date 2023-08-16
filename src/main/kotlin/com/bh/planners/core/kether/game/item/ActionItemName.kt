package com.bh.planners.core.kether.game.item

import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.read
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.nms.getName
import taboolib.type.BukkitEquipment
import java.util.concurrent.CompletableFuture

class ActionItemName(
    val slot: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<String>() {


    override fun run(frame: ScriptFrame): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        frame.read<BukkitEquipment>(slot).thenAccept { slot ->
            if (selector != null) {
                frame.createContainer(selector).thenAccept {
                    val entityTarget = it.firstLivingEntityTarget()
                    if (entityTarget != null) {
                        future.complete(slot.getItem(entityTarget)?.getName())
                    } else {
                        future.complete(null)
                    }
                }
            } else {
                future.complete(slot.getItem(frame.bukkitPlayer())?.getName())
            }
        }

        return future
    }

}