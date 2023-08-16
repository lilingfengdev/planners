package com.bh.planners.core.kether.common

import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import taboolib.common.platform.function.info
import taboolib.library.kether.*
import taboolib.module.kether.*


abstract class ParameterKetherParser(vararg id: String) : MultipleKetherParser(*id) {

    protected lateinit var argument: ParsedAction<*>

    override fun run(): QuestActionParser {
        return ScriptActionParser<Any?> {
            val argument = nextParsedAction()
            try {
                mark()
                val expects = expects(*this@ParameterKetherParser.method.keys.filter { it != "other" && it != "main" }.toTypedArray())
                val action = method[expects]!!.run().resolve<Any>(this)
                action
            } catch (ex: Exception) {
                reset()
                if (other == null) {
                    throw ex
                }
                other!!.run().resolve<Any>(this)
            }
        }
    }

    override fun toString(): String {
        return "ParameterKetherParser(argument=$argument)"
    }


}