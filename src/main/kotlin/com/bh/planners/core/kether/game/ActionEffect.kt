package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Effects
import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.toOriginLocation
import taboolib.common.platform.function.submit
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

object ActionEffect {

    // action FLAME 0 0 0 pos1 [ -@c-dot 3,0 ] pos2 [ -@c-dot 4,0 ]
    class Parser(val effect: com.bh.planners.core.effect.Effect, val action: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val future = CompletableFuture<Void>()

            frame.newFrame(action).run<Any>().thenAccept {
                try {
                    val context = frame.getContext()
                    future.complete(null)
                    submit(async = true) {
                        val effectOption = EffectOption(it.toString())
                        effect.sendTo(frame.toOriginLocation(), effectOption, context)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return future
        }
    }

    /**
     * effect 【loader: action】 [option: string>]
     * effect line "FLAME 0 0 0 -speed 1.0 -count 10 -@self"
     */
    @KetherParser(["effect"], namespace = NAMESPACE, shared = true)
    fun parser() = scriptParser {
        try {
            it.mark()
            val expect = it.expects(*Effects.effectKeys.toTypedArray())
            val effectLoader = Effects.get(expect)
            Parser(effectLoader, it.next(ArgTypes.ACTION))
        } catch (ex: Exception) {
            it.reset()
            throw ex
        }
    }

}
