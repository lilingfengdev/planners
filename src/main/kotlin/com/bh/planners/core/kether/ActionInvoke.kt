package com.bh.planners.core.kether

import com.bh.planners.api.ContextAPI
import com.bh.planners.api.script.ScriptLoader
import com.bh.planners.core.pojo.Skill
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionInvoke(val func: ParsedAction<*>, val using: List<ParsedAction<*>>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {


        val args = ArrayList<Any>()
        fun process(index: Int) {
            if (args.size < index) {
                frame.newFrame(using[index]).run<Any>().thenAccept {
                    args.add(it)
                    process(index + 1)
                }
            }
        }

        frame.run(func).str { func ->
            process(0)
            val skill = frame.skill().instance
            if (skill.actionMode == Skill.ActionMode.DEFAULT) {

                ScriptLoader.invokeFunction(ContextAPI.createSession(frame.asPlayer()!!, skill), func) { context ->
                    args.forEachIndexed { index, any ->
                        context.rootFrame().rootVariables()["arg$index"] = any
                    }
                }
            }
        }


        return CompletableFuture.completedFuture(null)
    }

    companion object {

        @KetherParser(["invoke"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val args = try {
                it.mark()
                it.next(ArgTypes.listOf(ArgTypes.ACTION))
            } catch (e: Exception) {
                it.reset()
                emptyList<ParsedAction<*>>()
            }
            ActionInvoke(it.nextParsedAction(),args)
        }

    }


}