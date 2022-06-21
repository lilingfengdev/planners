package com.bh.planners.core.kether.event

import com.bh.planners.core.kether.ActionEvent.Companion.event
import com.bh.planners.core.kether.eventParser
import org.bukkit.event.Cancellable
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
class ActionEventCancel(val action: ParsedAction<*>) : ScriptAction<Boolean>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Boolean> {
        val event = frame.event()
        if (event is Cancellable) {
            return frame.newFrame(action).run<Any>().thenApply {
                event.isCancelled = Coerce.toBoolean(it)
                event.isCancelled
            }
        }
        return CompletableFuture.completedFuture(false)
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