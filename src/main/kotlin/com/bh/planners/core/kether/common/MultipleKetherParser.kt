package com.bh.planners.core.kether.common

import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*

abstract class MultipleKetherParser : CombinationKetherParser {
    
    private val method = mutableMapOf<String,CombinationKetherParser>()
    
    fun case(vararg str: String,func: () -> ScriptActionParser<Any?>) {
        
        val parser = object : SimpleKetherParser(*str) {

            override fun run(): ScriptActionParser<Any?> {
                return func()
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
                        parser.run().resolve<Any>(this) as ScriptAction<*>
                    }
                }
            }
        }
    }
    
}