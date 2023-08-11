package com.bh.planners.core.kether.common

import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

abstract class MultipleKetherParser(vararg id: String) : SimpleKetherParser(*id) {

    private val method = mutableMapOf<String, CombinationKetherParser>()

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

    override fun toString(): String {
        return "MultipleKetherParser(method=$method)"
    }


}