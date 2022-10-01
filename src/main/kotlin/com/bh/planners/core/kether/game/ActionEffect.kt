package com.bh.planners.core.kether.game

import com.bh.planners.core.effect.Effect
import com.bh.planners.core.effect.EffectOption
import com.bh.planners.core.effect.Effects
import com.bh.planners.core.effect.inline.Incident.Companion.handleIncident
import com.bh.planners.core.effect.inline.IncidentEffectTick
import com.bh.planners.core.kether.*
import com.bh.planners.core.pojo.Session
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.material.MaterialData
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestFuture
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

object ActionEffect {

    // effect action ""
    class Parser(val effect: Effect, val action: ParsedAction<*>, val onTick: ParsedAction<*>) : ScriptAction<Void>() {

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(action).str { action ->
                val context = frame.getContext()
                frame.run(onTick).str { ontick ->

                    val response = Response(frame.getSession(), ontick)

                    submit(async = true) {
                        val effectOption = EffectOption.get(action)
                        effect.sendTo(frame.toOriginLocation(), effectOption, context, response)
                    }

                }

            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class Response(val session: Session, val name: String) {


        fun onTick(locations: List<Location>) {

            if (name == "none") return

            val effectTick = IncidentEffectTick(locations)
            session.handleIncident(name, effectTick)
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
            Parser(effectLoader, it.nextParsedAction(), it.tryGet(arrayOf("ontick", "onTick"), "none")!!)
        } catch (ex: Exception) {
            it.reset()
            throw ex
        }
    }

}
