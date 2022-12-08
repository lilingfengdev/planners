package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.*
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Session
import org.bukkit.Location
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

object ActionEffect {

    // effect action ""
    class Parser(val effect: Effect, val action: ParsedAction<*>, val onTick: ParsedAction<*>, val onHit: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(action).str { action ->
                val context = frame.getContext()
                frame.run(onTick).str { ontick ->

                    frame.run(onHit).str { onhit ->

                        val response = Response(frame.getSession())

                        response.tick = EffectICallback.Tick(ontick,response.session)
                        response.hit = EffectICallback.Hit(onhit,response.session)

                        submit(async = true) {
                            val effectOption = EffectOption.get(action)
                            effect.sendTo(frame.toOriginLocation(), effectOption, context, response)
                        }
                    }



                }

            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class Response(val session: Session) {


        var tick: EffectICallback.Tick? = null

        var hit: EffectICallback.Hit? = null

        fun handleTick(location: Location) {
            this.handleTick(listOf(location))
        }

        fun handleTick(locations: List<Location>) {

            this.tick?.handle(locations)
            this.hit?.handle(locations)
        }

    }

    /**
     * effect 【loader: action】 [option: string>]
     * effect line "FLAME 0 0 0 -speed 1.0 -count 10 -@self" ontick
     */
    @KetherParser(["effect"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        try {
            it.mark()
            val expect = it.expects(*Effects.effectKeys.toTypedArray())
            val effectLoader = Effects.get(expect)
            Parser(effectLoader, it.nextParsedAction(),
                it.tryGet(arrayOf("ontick", "onTick"), "none")!!,
                it.tryGet(arrayOf("onhit", "onHit"), "none")!!
            )
        } catch (ex: Exception) {
            it.reset()
            throw ex
        }
    }

}
