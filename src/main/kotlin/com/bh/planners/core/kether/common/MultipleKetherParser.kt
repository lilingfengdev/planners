package com.bh.planners.core.kether.common

import taboolib.common.platform.function.info
import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.library.reflex.ReflexClass
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

abstract class MultipleKetherParser(vararg id: String) : SimpleKetherParser(*id), Stateable {

    protected val method = mutableMapOf<String, CombinationKetherParser>()

    private val other: CombinationKetherParser?
        get() = method["main"] ?: method["other"]

    override fun run(): ScriptActionParser<Any?> {
        return ScriptActionParser {
            try {
                mark()
                val expects = expects(*this@MultipleKetherParser.method.keys.filter { it != "other" && it != "main" }.toTypedArray())
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

    @Suppress("UNCHECKED_CAST")
    override fun onInit() {
        ReflexClass.of(this::class.java).structure.fields.forEach { field ->
            if (field.name == "INSTANCE") return@forEach

            // parameter parser
            if (ParameterKetherParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = field.get(this) as ParameterKetherParser
                // 子集初始化
                parser.onInit()

                this.method[field.name] = parser
            }
            // combination parser
            else if (CombinationKetherParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = field.get(this) as CombinationKetherParser

                // 子集初始化
                if (parser is Stateable) {
                    parser.onInit()
                }

                // 去重
                setOf(*parser.id, field.name).forEach {
                    this.method[it] = parser
                }
            }
            // scriptParser combinationParser
            else if (ScriptActionParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = field.get(this) as ScriptActionParser<Any>
                this.method[field.name] = simpleKetherParser(field.name) {
                    scriptParser { parser.resolve<Any>(it) }
                }
            }
        }
    }

    override fun toString(): String {
        return "MultipleKetherParser(method=$method)"
    }


}