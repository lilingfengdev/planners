package com.bh.planners.core.kether.common

import com.bh.planners.util.Reflexs
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestActionParser
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*

abstract class ParameterKetherParser(vararg id: String) : SimpleKetherParser(*id), Stateable {

    protected val method = mutableMapOf<String, ArgumentKetherParser>()

    protected val mainParser: ArgumentKetherParser?
        get() = method["main"] ?: method["other"]

    override fun run(): QuestActionParser {
        return scriptParser {
            val argument = it.nextParsedAction()
            it.switch {
                this@ParameterKetherParser.method.forEach { (id, parser) ->
                    case(id) { parser.parser.invoke(it, argument) }
                }
                other { mainParser?.parser?.invoke(it, argument) }
            }
        }
    }

    override fun onInit() {
        Reflexs.getFields(this::class.java).forEach { field ->

            // ignored ...
            if (field.name == "INSTANCE" || field.isAnnotationPresent(CombinationKetherParser.Ignore::class.java)) {
                return@forEach
            }

            // parameter parser
            if (ArgumentKetherParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = field.get(this) as ArgumentKetherParser
                setOf(field.name, *parser.id).forEach {
                    this.method[it] = parser
                }
            }
        }
    }

    protected fun argumentKetherParser(vararg id: String, func: QuestReader.(argument: ParsedAction<*>) -> ScriptAction<*>): ArgumentKetherParser {
        return ArgumentKetherParser(arrayOf(*id), func)
    }

    protected fun argumentKetherNow(vararg id: String, func: ScriptFrame.(argument: Any?) -> Any?): ArgumentKetherParser {
        return argumentKetherParser(*id) { argument ->
            actionNow {
                run(argument).thenApply { func(this, it) }
            }
        }
    }

    data class ArgumentKetherParser(val id: Array<String>, val parser: QuestReader.(argument: ParsedAction<*>) -> ScriptAction<*>)


}