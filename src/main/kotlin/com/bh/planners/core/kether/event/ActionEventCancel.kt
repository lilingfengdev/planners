package com.bh.planners.core.kether.event

import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import com.bh.planners.core.kether.read
import com.bh.planners.core.kether.nextArgumentAction
import org.bukkit.event.Cancellable
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * 事件取消
 * event cancel [to [false/true]]
 */
class ActionEventCancel(val action: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val event = frame.event()
        val future = CompletableFuture<Void>()
        if (event is Cancellable) {
            frame.read<Boolean>(action).thenAccept { value ->
                event.isCancelled = value
                future.complete(null)
            }
        } else {
            future.complete(null)
        }
        return future
    }

    companion object {

        @KetherParser(["cancel"])
        fun parser() = eventParser {
            ActionEventCancel(it.nextArgumentAction(arrayOf("to"),true)!!)
        }

    }


}