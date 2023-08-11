package com.bh.planners.core.kether.common

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.container
import com.bh.planners.core.kether.containerOrOrigin
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.library.kether.Parser
import taboolib.library.kether.QuestContext
import taboolib.library.kether.QuestReader
import taboolib.module.kether.KetherLoader
import taboolib.module.kether.ParserHolder
import taboolib.module.kether.ScriptActionParser
import taboolib.module.kether.combinationParser
import java.util.function.Supplier

interface CombinationKetherParser {

    val id: Array<String>

    val namespace: String

    fun run(): ScriptActionParser<Any?>

    annotation class Use

    companion object {

        @Awake
        class Visitor : ClassVisitor(0) {

            override fun getLifeCycle(): LifeCycle {
                return LifeCycle.LOAD
            }

            override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
                if (clazz.isAnnotationPresent(Use::class.java) && CombinationKetherParser::class.java.isAssignableFrom(clazz)) {
                    val combinationKetherParser = instance!!.get() as CombinationKetherParser
                    val parser = combinationKetherParser.run()
                    val id = combinationKetherParser.id
                    val namespace = combinationKetherParser.namespace
                    KetherLoader.registerParser(parser, id, namespace, true)
                }
            }

        }

    }

    /**
     * 返回至少是释放者的目标容器
     */
    fun ParserHolder.containerOrSender() : Parser<Target.Container> {
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
    fun ParserHolder.containerOrOrigin() : Parser<Target.Container> {
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
    fun ParserHolder.containerOrEmpty() : Parser<Target.Container?> {
        return Parser.frame {
            val nextSelectorOrNull = it.nextSelectorOrNull()
            Parser.Action { frame ->
                frame.container(nextSelectorOrNull)
            }
        }
    }
    
}