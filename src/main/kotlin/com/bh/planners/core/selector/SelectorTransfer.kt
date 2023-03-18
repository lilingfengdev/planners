package com.bh.planners.core.selector

import com.bh.planners.api.common.Demand
import com.bh.planners.core.effect.Target
import com.bh.planners.core.pojo.Context
import java.util.concurrent.CompletableFuture

class SelectorTransfer(val context: Context, val source: String, val container: Target.Container) {

    val selectorParsed = mutableListOf<ParsedSelector>()

    init {
        val split = source.split(" ")
        val values = mutableListOf<String>()
        var id: String? = null
        var at = false
        split.forEachIndexed { index, s ->
            if (s[0] == '@' || s[1] == '@') {
                // 保留上一条缓存
                if (at) {
                    selectorParsed += ParsedSelector(id!!, values.joinToString(" "))
                }
                at = true
                id = s.substring(s.indexOfFirst { it == '@' } + 1)
            }
            // 缓存参数
            else if (at) {
                values += s
            }
            if (index == split.lastIndex && at) {
                at = false
                selectorParsed += ParsedSelector(id!!, values.joinToString(" "))
            }
        }
    }

    fun run(): CompletableFuture<Void> {
        return process(0, CompletableFuture())
    }

    fun process(index: Int, future: CompletableFuture<Void>): CompletableFuture<Void> {
        val parsedSelector = selectorParsed[index]
        parsedSelector.bind.check(Selector.Data(parsedSelector.namespace, parsedSelector.value, context, container)).thenAccept {

            if (index == selectorParsed.lastIndex) {
                future.complete(null)
            } else {
                process(index + 1, future)
            }

        }
        return future
    }


    class ParsedSelector(val namespace: String, val value: String) {

        val bind = Selector.getSelector(namespace)

    }

}