package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.platform.util.sendActionBar
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionActionBar(val message: ParsedAction<*>, val selector: ParsedAction<*>?) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.run(message).str { message ->
            frame.containerOrSender(selector).thenAccept {
                it.forEachPlayer { sendActionBar(message.trimIndent().replace("@sender", this.name)) }
            }
        }
        return CompletableFuture.completedFuture(null)
    }


    internal object Parser {

        @KetherParser(["actionbar"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionActionBar(it.nextParsedAction(), it.nextSelectorOrNull())
        }
    }
}