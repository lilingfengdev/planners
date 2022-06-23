package com.bh.planners.core.kether

import com.bh.planners.core.skill.effect.Effect
import com.bh.planners.core.skill.effect.EffectArc
import com.bh.planners.core.skill.effect.EffectOption
import com.bh.planners.core.skill.effect.Effects
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

object ActionEffect {

    class Parser(val effect: Effect, val action: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(action).run<String>().thenAccept {
                try {
                    val session = frame.getSession()
                    val effectOption = EffectOption(it)
                    effect.sendTo(frame.toOriginLocation(), effectOption, session)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    /**
     * effect <loader> <option: string>
     * effect line "FLAME 0 0 0 -speed 1.0 -count 10 -@self"
     */
    @KetherParser(["effect"], namespace = NAMESPACE)
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
