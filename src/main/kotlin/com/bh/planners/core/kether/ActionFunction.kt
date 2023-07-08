package com.bh.planners.core.kether

import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.KetherFunction.parse
import java.util.concurrent.CompletableFuture

/**
 * function "your name is {{player name}}"
 * @author IzzelAliz
 */
class ActionFunction(val source: ParsedAction<*>) : ScriptAction<String>() {

    override fun run(frame: ScriptFrame): CompletableFuture<String> {
        val vars = frame.deepVars()
        return frame.newFrame(source).run<Any>().thenApply {
            try {
                parse(it.toString().trimIndent(), ScriptOptions.builder().namespace(namespace = namespaces).context {
                    vars.forEach { (k, v) -> rootFrame().variables().set(k, v) }
                }.build())
            } catch (e: Exception) {
                e.printKetherErrorMessage()
                info("Error kether script = $it")
                it.toString()
            }
        }
    }

    internal object Parser {

        @KetherParser(["inline", "function"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionFunction(it.nextParsedAction())
        }
    }
}