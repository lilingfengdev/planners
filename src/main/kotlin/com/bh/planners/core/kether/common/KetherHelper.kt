package com.bh.planners.core.kether.common

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.*
import com.mojang.datafixers.kinds.App
import org.bukkit.Material
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.Parser
import taboolib.library.kether.QuestActionParser
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import taboolib.module.kether.ParserHolder.option
import java.util.Optional
import java.util.concurrent.CompletableFuture

/**
 * object的原因是供给外部使用
 */
object KetherHelper {


    fun ParserHolder.containerOrElse(func: ScriptFrame.() -> Target.Container): Parser<Target.Container> {
        return Parser.frame { r ->
            val action = r.nextSelectorOrNull()
            future {
                // 如果为空 则返回else容器
                if (action == null) {
                    CompletableFuture.completedFuture(func(this))
                }
                // 默认返回容器
                else {
                    container(action)
                }
            }
        }
    }

    fun newTargetContainer(target: Target): Target.Container {
        return Target.Container().also {
            it += target
        }
    }

    /**
     * 返回至少是释放者的目标容器
     */
    fun ParserHolder.containerOrSender(): Parser<Target.Container> {
        return containerOrElse { newTargetContainer(getContext().sender) }
    }

    /**
     * 返回至少是原点的目标容器
     */
    fun ParserHolder.containerOrOrigin(): Parser<Target.Container> {
        return containerOrElse { newTargetContainer(origin()) }
    }

    /**
     * 返回有可能为空的目标容器
     */
    fun ParserHolder.containerOrEmpty(): Parser<Target.Container> {
        return containerOrElse { Target.Container() }
    }


    fun ParserHolder.materialOrStone() = material(Material.STONE)

    fun ParserHolder.materialOrBarrier() = material(Material.BARRIER)

    fun ParserHolder.material(default: Material) = any().map { Material.valueOf(it?.toString() ?: return@map null) }.defaultsTo(default)

    fun simpleKetherParser(vararg id: String, func: () -> ScriptActionParser<out Any?>): SimpleKetherParser {
        return object : SimpleKetherParser(*id) {
            override fun run(): ScriptActionParser<out Any?> {
                return func()
            }
        }
    }

    fun simpleKetherNow(vararg id: String, func: ScriptFrame.() -> Any?): SimpleKetherParser {
        return simpleKetherParser(*id) {
            scriptParser { actionNow { func(this) } }
        }
    }

    fun <T> simpleKetherParser(vararg id: String, builder: ParserHolder.(Parser.Instance) -> App<Parser.Mu, Parser.Action<T>>): SimpleKetherParser {
        return object : SimpleKetherParser(*id) {
            override fun run(): ScriptActionParser<Any?> {
                return ScriptActionParser {
                    Parser.build(builder(ParserHolder, Parser.instance())).resolve<Any?>(this)
                }
            }
        }
    }

    fun parameterKetherParser(vararg id: String, func: QuestReader.(argument: ParsedAction<*>) -> ScriptAction<*>): SimpleKetherParser {
        return object : SimpleKetherParser(*id) {

            override fun run(): QuestActionParser {
                return scriptParser { func(it, it.nextParsedAction()) }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun registerCombinationKetherParser(id: String, combinationKetherParser: CombinationKetherParser) {
        val id = arrayOf(id, *combinationKetherParser.id)
        val namespace = combinationKetherParser.namespace
        if (combinationKetherParser is Stateable) {
            combinationKetherParser.onInit()
        }
        registerCombinationKetherParser(id, namespace, combinationKetherParser.run() as ScriptActionParser<Any?>)
    }

    fun registerCombinationKetherParser(id: String, namespace: String, parser: ScriptActionParser<Any?>) {
        registerCombinationKetherParser(arrayOf(id), namespace, parser)
    }

    fun registerCombinationKetherParser(id: Array<String>, namespace: String, parser: ScriptActionParser<Any?>) {
        KetherLoader.registerParser(parser, id, namespace, true)
    }

    fun registerCombinationKetherParser(combinationKetherParser: CombinationKetherParser) {
        val id = combinationKetherParser.id
        val namespace = combinationKetherParser.namespace
        if (combinationKetherParser is Stateable) {
            combinationKetherParser.onInit()
        }
        KetherLoader.registerParser(combinationKetherParser.run() as ScriptActionParser<*>, id, namespace, true)
    }

}