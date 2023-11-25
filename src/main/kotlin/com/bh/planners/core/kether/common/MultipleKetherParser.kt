package com.bh.planners.core.kether.common

import com.bh.planners.core.kether.common.KetherHelper.simpleKetherParser
import taboolib.library.kether.QuestActionParser
import taboolib.library.reflex.ClassAnalyser
import taboolib.module.kether.ScriptActionParser
import taboolib.module.kether.expects

abstract class MultipleKetherParser(vararg id: String) : SimpleKetherParser(*id), Stateable {

    protected val method = mutableMapOf<String, CombinationKetherParser>()

    protected val other: CombinationKetherParser?
        get() = method["main"] ?: method["other"]

    override fun run(): QuestActionParser {
        return ScriptActionParser<Any?> {
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
        ClassAnalyser.analyseByReflection(this::class.java).fields.forEach { field ->
            // ignored ...
            if (field.name == "INSTANCE" || field.isAnnotationPresent(CombinationKetherParser.Ignore::class.java)) {
                return@forEach
            }
            // combination parser
            else if (CombinationKetherParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = field.get(this) as CombinationKetherParser
                registerInternalCombinationParser(arrayOf(field.name), parser)
            }
            // scriptParser combinationParser
            else if (ScriptActionParser::class.java.isAssignableFrom(field.fieldType)) {
                val parser = simpleKetherParser(field.name) {
                    field.get(this) as ScriptActionParser<Any>
                }
                registerInternalCombinationParser(arrayOf(field.name), parser)
            }

        }

    }

    protected fun registerInternalCombinationParser(id: Array<String>, combinationKetherParser: CombinationKetherParser) {
        // 子集初始化
        if (combinationKetherParser is Stateable) {
            combinationKetherParser.onInit()
        }
        // 去重
        setOf(*id, *combinationKetherParser.id).forEach {
            this.method[it] = combinationKetherParser
        }
    }

    override fun toString(): String {
        return "MultipleKetherParser(method=$method)"
    }


}