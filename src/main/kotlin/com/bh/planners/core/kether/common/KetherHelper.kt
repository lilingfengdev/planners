package com.bh.planners.core.kether.common

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.container
import com.bh.planners.core.kether.containerOrOrigin
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import com.mojang.datafixers.kinds.App
import ink.ptms.adyeshach.impl.script.expects
import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*


/**
 * 返回至少是释放者的目标容器
 */
fun ParserHolder.containerOrSender(): Parser<Target.Container> {
    return Parser.frame {
        val nextSelectorOrNull = it.nextSelectorOrNull()
        Parser.Action { frame ->
            frame.containerOrSender(nextSelectorOrNull)
        }
    }
}

/**
 * 返回至少是原点的目标容器
 */
fun ParserHolder.containerOrOrigin(): Parser<Target.Container> {
    return Parser.frame {
        val nextSelectorOrNull = it.nextSelectorOrNull()
        Parser.Action { frame ->
            frame.containerOrOrigin(nextSelectorOrNull)
        }
    }
}

/**
 * 返回有可能为空的目标容器
 */
fun ParserHolder.containerOrEmpty(): Parser<Target.Container> {
    return Parser.frame {
        val nextSelectorOrNull = it.nextSelectorOrNull()
        Parser.Action { frame ->
            frame.container(nextSelectorOrNull)
        }
    }
}

fun simpleKetherParser(vararg id: String, func: () -> ScriptActionParser<out Any?>): SimpleKetherParser {
    return object : SimpleKetherParser(*id) {
        override fun run(): ScriptActionParser<out Any?> {
            return func()
        }
    }
}

fun simpleKetherNow(vararg id: String,func : ScriptFrame.() -> Any?) : SimpleKetherParser {
    return simpleKetherParser(*id) {
        scriptParser { actionNow { func(this) } }
    }
}

fun <T> simpleKetherParser(vararg id: String, builder: ParserHolder.(Parser.Instance) -> App<Parser.Mu, Parser.Action<T>>): SimpleKetherParser {
    return object : SimpleKetherParser(*id) {
        override fun run(): ScriptActionParser<Any?> {
            return ScriptActionParser {
                Parser.build(builder(ParserHolder,Parser.instance())).resolve<Any?>(this)
            }
        }
    }
}
