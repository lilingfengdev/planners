package com.bh.planners.core.kether

import com.bh.planners.core.kether.common.CombinationKetherParser
import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import taboolib.module.kether.KetherFunction.parse
import java.util.concurrent.CompletableFuture

/**
 * function "your name is {{player name}}"
 * @author IzzelAliz
 */
//@CombinationKetherParser.Used
//fun inlineText() = simpleKetherParser<String>("function", "inline", "text") {
//    it.group(text()).apply(it) { text ->
//        now {
//            val vars = deepVars()
//            try {
//                parse(it.toString().trimIndent(), ScriptOptions.builder().namespace(namespace = listOf(NAMESPACE)).context {
//                    vars.forEach { (k, v) -> rootFrame().variables().set(k, v) }
//                }.build())
//            } catch (e: Exception) {
//                e.printKetherErrorMessage()
//                info("Error kether script = $it")
//                it.toString()
//            }
//        }
//    }
//}