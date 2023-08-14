package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.api.common.ParticleFrame.Companion.new
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.containerOrOrigin
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.kether.game.ActionEffect.maps
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.nextArgumentAction
import com.bh.planners.core.kether.origin
import com.bh.planners.core.pojo.Context
import taboolib.common.platform.function.submit
import taboolib.library.kether.ParsedAction
import taboolib.library.kether.QuestReader
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.run
import taboolib.module.kether.str
import java.util.concurrent.CompletableFuture

/**
 * effect tie-line "<option>" pos1? "@self" pos2? "@self"
 */
object EffectTieLine : Effect(), EffectParser {

    override val name: String
        get() = "tie-line"

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

    }

    class Spawner(val pos1: Target, val pos2: Target) : Effect() {

        override val name: String
            get() = "tie-line:impl"

        override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

            val effectSpawner = EffectSpawner(option)

            ParticleFrame.create(ParticleFrame.FrameBuilder().new {
                time(option.period)
                response(response)
                builder(EffectLine.Builder(pos1.getLocation()!!, pos2.getLocation()!!, option.step, effectSpawner))
            })

        }

    }

    override fun parser(reader: QuestReader): ScriptAction<*> {
        val option = reader.nextParsedAction()
        val pos1 = reader.nextArgumentAction(arrayOf("pos1"), "@self")!!
        val pos2 = reader.nextArgumentAction(arrayOf("pos2"), "@self")!!
        val events = reader.maps()
        return Action(EffectTieLine, option).also {
            it.events += events
            it.pos1 = pos1
            it.pos2 = pos2
        }
    }

    class Action(effect: Effect, action: ParsedAction<*>) : ActionEffect.Parser(effect, action) {

        lateinit var pos1: ParsedAction<*>
        lateinit var pos2: ParsedAction<*>

        override fun run(frame: ScriptFrame): CompletableFuture<Void> {

            frame.run(action).str { action ->
                frame.containerOrOrigin(pos1).thenAccept { pos1 ->
                    frame.containerOrOrigin(pos2).thenAccept last@{ pos2 ->
                        val context = frame.getContext()

                        if (context !is Context.SourceImpl) return@last

                        val response = ActionEffect.Response(context, events)
                        val spawner = Spawner(pos1.firstLocationTarget()!!, pos2.firstLocationTarget()!!)
                        submit(async = true) {
                            spawner.sendTo(frame.origin(), EffectOption.get(action), context, response)
                        }
                    }
                }

            }

            return CompletableFuture.completedFuture(null)
        }

    }

}