package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.*
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.origin
import com.bh.planners.core.pojo.Context
import org.bukkit.Location
import taboolib.common.platform.function.submitAsync
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestReader
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.set

object ActionEffect {

    // effect action ""
    open class Parser(val effect: Effect, val action: ParsedAction<*>) : ScriptAction<Void>() {

        val events = mutableMapOf<String, String>()

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(action).str { action ->
                val context = frame.getContext()

                if (context !is Context.SourceImpl) return@str

                val response = Response(context, events)

                submitAsync {
                    val effectOption = EffectOption.get(action)
                    effect.sendTo(frame.origin(), effectOption, context, response)
                }

            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class Response(val context: Context.SourceImpl, events: Map<String, String>) {

        val eventTicks = mutableListOf<EffectICallback<*>>(
            EffectICallback.Tick(events["ontick"] ?: "__null__", context),
            EffectICallback.Hit(events["onhit"] ?: "__null__", context)
        )

        fun handleTick(location: Location) {
            this.handleTick(listOf(location))
        }

        fun handleTick(locations: List<Location>) {
            eventTicks.forEach { it.onTick(locations) }
        }

    }

    /**
     * effect 【loader: action】 [option: string>]
     * effect line "FLAME 0 0 0 -speed 1.0 -count 10 @self" ontick
     */
    @KetherParser(["effect"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        try {
            it.mark()
            val expect = it.expects(*Effects.effectKeys.toTypedArray())
            val effectLoader = Effects.get(expect)
            // 优先解析特殊粒子解析器
            if (effectLoader is EffectParser) {
                effectLoader.parser(it)
            }
            // 粒子默认解析器
            else {
                val option = it.nextParsedAction()
                val events = it.maps()

                Parser(effectLoader, option).also {
                    it.events += events
                }
            }
        } catch (e: Exception) {
            it.reset()
            throw e
        }
    }

    /**
     * 尝试获取一个maps
     */
    fun QuestReader.maps(): MutableMap<String, String> {
        val mapOf = mutableMapOf<String, String>()
        while (true) {
            this.mark()
            val nextToken = this.nextToken()
            if (nextToken.startsWith("on")) {
                mapOf[nextToken] = this.nextToken()
            } else {
                this.reset()
                break
            }
        }
        return mapOf
    }

}
