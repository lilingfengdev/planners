package com.bh.planners.core.kether.common

import taboolib.library.kether.*
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture


abstract class ParameterKetherParser(vararg id: String) : MultipleKetherParser(*id) {

    private lateinit var argument: ParsedAction<*>

    override fun run(): ScriptActionParser<Any?> {
        return scriptParser {
            this.argument = it.nextParsedAction()
            super.run().resolve(it)
        }
    }

    protected fun <T> argumentNow(action: ScriptFrame.(argument: Any) -> T): Parser.Action<T> {
        return Parser.Action {
            it.run(argument).thenApply { argument ->
                action(it, argument!!)
            }
        }
    }

    override fun toString(): String {
        return "ParameterKetherParser(argument=$argument)"
    }


}