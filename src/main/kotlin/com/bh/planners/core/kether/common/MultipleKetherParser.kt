package com.bh.planners.core.kether.common

import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.library.reflex.ReflexClass
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

abstract class MultipleKetherParser(vararg id: String) : SimpleKetherParser(*id) {

    private val method = mutableMapOf<String, CombinationKetherParser>()

    init {
        this.init()
    }

    fun case(vararg str: String, func: () -> ScriptActionParser<Any?>) {

        val parser = object : SimpleKetherParser(*str) {

            override fun run(): ScriptActionParser<Any?> {
                return func()
            }

            override fun toString(): String {
                return "InternalParser"
            }

        }

        str.forEach { name ->
            method[name] = parser
        }
    }

    override fun run(): ScriptActionParser<Any?> {
        return ScriptActionParser {

            this.switch {
                this@MultipleKetherParser.method.forEach { (name, parser) ->
                    case(name) {

                        object : ScriptAction<Any?>() {
                            override fun run(frame: ScriptFrame): CompletableFuture<Any?> {
                                return parser.run().resolve<Any?>(this@case).process(frame)
                            }

                            override fun toString(): String {
                                return "ActionDSL(MultipleKetherParser)"
                            }

                        }
                    }
                }
            }
        }
    }

    private fun init() {
        ReflexClass.of(this::class.java).structure.fields.forEach { field ->
            if (field.fieldType == CombinationKetherParser::class.java) {
                val parser = field.get(this) as CombinationKetherParser
                // 去重
                setOf(*parser.id,field.name).forEach {
                    this.method[it] = parser
                }
            }
        }
    }

    override fun toString(): String {
        return "MultipleKetherParser(method=$method)"
    }


}