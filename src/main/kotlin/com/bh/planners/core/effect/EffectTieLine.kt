package com.bh.planners.core.effect

import com.bh.planners.api.common.ParticleFrame
import com.bh.planners.core.effect.EffectTieLine.step
import com.bh.planners.core.pojo.Context
import com.bh.planners.core.effect.Target.Companion.createContainer
import com.bh.planners.core.effect.Target.Companion.getLocation
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.game.ActionEffect
import com.bh.planners.core.kether.game.ActionEffect.maps
import com.bh.planners.core.kether.getContext
import com.bh.planners.core.kether.origin
import com.bh.planners.core.kether.tryGet
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
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

    val EffectOption.step: Double
        get() = Coerce.toDouble(this.demand.get(Effects.STEP, "1.0"))

    override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

    }

    class Spawner : Effect() {

        override val name: String
            get() = "tie-line:impl"

        lateinit var pos1: Target
        lateinit var pos2: Target


        override fun sendTo(target: Target?, option: EffectOption, context: Context, response: ActionEffect.Response) {

            val effectSpawner = EffectSpawner(option)
            val period = option.period
            val step = option.step

            val builder =
                EffectLine.Builder(pos1.getLocation() ?: return, pos2.getLocation() ?: return, step, effectSpawner)
            ParticleFrame.create(period, builder, response)
        }

    }

    override fun parser(reader: QuestReader): ScriptAction<*> {
        val option = reader.nextParsedAction()
        val pos1 = reader.tryGet(arrayOf("pos1"),"@self")!!
        val pos2 = reader.tryGet(arrayOf("pos2"),"@self")!!
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
                frame.createContainer(pos1).thenAccept { pos1 ->
                    frame.createContainer(pos2).thenAccept { pos2 ->
                        val context = frame.getContext()

                        if (context !is Context.SourceImpl) return@thenAccept

                        val response = ActionEffect.Response(context, events)
                        val spawner = Spawner()
                        spawner.pos1 = pos1.firstLocationTarget() ?: frame.origin()
                        spawner.pos2 = pos2.firstLocationTarget() ?: frame.origin()
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