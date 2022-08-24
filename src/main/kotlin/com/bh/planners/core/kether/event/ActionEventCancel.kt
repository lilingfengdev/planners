package com.bh.planners.core.kether.event

import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import com.bh.planners.core.kether.runTransfer
import org.bukkit.Bukkit
import org.bukkit.event.Cancellable
import taboolib.common.platform.function.info
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestReader
import taboolib.library.kether.actions.LiteralAction
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
            frame.runTransfer<Boolean>(action).thenAccept { value ->
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
            val action = try {
                it.mark()
                it.expect("to")
                it.next(ArgTypes.ACTION)
            } catch (_: Exception) {
                it.reset()
                ParsedAction(LiteralAction<Boolean>("true"))
            }
            ActionEventCancel(action)
        }

    }


}