package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.chant.ChantBuilder
import com.bh.planners.core.pojo.chant.Interrupt
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionChant : ScriptAction<Void>() {

    lateinit var builder: ParsedAction<*>
    lateinit var duration: ParsedAction<*>
    lateinit var period: ParsedAction<*>
    lateinit var delay: ParsedAction<*>
    lateinit var async: ParsedAction<*>
    var selector: ParsedAction<*>? = null

    var then: ParsedAction<*>? = null


    val interruptTags = mutableListOf<Interrupt>()

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        frame.run(builder).str { builderId ->
            frame.run(duration).long { duration ->
                frame.run(period).long { period ->
                    frame.run(delay).long { delay ->
                        frame.run(async).bool { async ->
                            frame.containerOrSender(selector).thenAccept { container ->
                                try {
                                    val context = frame.session()
                                    val instance = ChantBuilder.newInstance(builderId, duration, period, delay, async)
                                    val futures = container.mapOfPlayer {
                                        instance.build(this,interruptTags, context) { value ->
                                            if (then != null) {
                                                frame.variables()["pointer"] = value
                                                frame.run(then!!)
                                            }
                                        }
                                    }
                                    CompletableFuture.allOf(*futures.toTypedArray()).thenAccept {
                                        frame.variables().remove("pointer")
                                        future.complete(null)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
            }
        }
        return future
    }

    companion object {


        /**
         * chant <builderId> <duration> <period> <delay> <async: true> <interrupt tag...(--unmove)> <selector: @self> <then action>
         */
        @KetherParser(["chant"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val builder = it.nextParsedAction()
            val duration = it.nextParsedAction()
            val period = it.nextParsedAction()
            val delay = it.nextOptionalParsedAction("delay", 0)!!
            val async = it.nextOptionalParsedAction("async", true)!!
            val tags = mutableListOf<Interrupt>()
            while (true) {
                try {
                    it.mark()
                    tags += Interrupt.getInterrupt(it.expects(*Interrupt.getInterruptKeys().map { "--un$it" }.toTypedArray()))
                } catch (e: Exception) {
                    it.reset()
                    break
                }
            }
            val selector = it.nextSelectorOrNull()
            val then = it.nextOptionalParsedAction("then")
            ActionChant().also {
                it.builder = builder
                it.duration = duration
                it.period = period
                it.delay = delay
                it.async = async
                it.interruptTags += tags
                it.selector = selector
                it.then = then
            }
        }

    }


}