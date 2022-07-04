package com.bh.planners.core.kether

import com.bh.planners.core.skill.effect.Target
import com.bh.planners.core.kether.selector.Fetch.asContainer
import com.bh.planners.core.pojo.data.DataContainer.Companion.unsafeData
import taboolib.common.platform.function.info
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionSelector {

    class ActionTargetContainerGetSize(val action: ParsedAction<*>) : ScriptAction<Int>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.getContext().flags.get(it.toString())?.asContainer()?.size ?: 0
            }
        }

    }

    class ActionTargetContainerGet(val action: ParsedAction<*>) : ScriptAction<Target.Container?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container?> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.getContext().flags.get(it.toString())?.asContainer()
            }
        }

    }

    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val future = CompletableFuture<Void>()
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.createTargets(valueAction).thenAccept { container ->
                    frame.getContext().flags[key] = container.unsafeData()
                    future.complete(null)
                }
            }
            return future
        }
    }

    class ActionTargetContainerRemove(val keyAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.getContext().flags.remove(key)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionTargetContainerList(val key: ParsedAction<*>) : ScriptAction<Set<Target>>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Set<Target>> {

            return frame.newFrame(key).run<String>().thenApply {
                frame.getContext().flags.get(it.toString())?.asContainer()?.targets ?: emptySet()
            }

        }
    }

    companion object {

        /**
         * 缓存目标容器
         * selector [key] to [selector]
         * selector g0 to "-@range 10 10 10"
         *
         * 删除
         * selector [key] remove
         *
         * 列表
         * selector [key] list
         *
         * 取
         * selector [key]
         * selector [key] size
         */
        @KetherParser(["selector"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val key = it.next(ArgTypes.ACTION)
            try {
                it.mark()
                when (it.expects("to", "set", "remove", "size", "list")) {
                    "to", "set" -> ActionTargetContainerSet(key, it.next(ArgTypes.ACTION))
                    "remove" -> ActionTargetContainerRemove(key)
                    "size" -> ActionTargetContainerGetSize(key)
                    "list" -> ActionTargetContainerList(key)
                    "get" -> ActionTargetContainerGet(key)
                    else -> error("error of case!")
                }
            } catch (_: Exception) {
                it.reset()
                ActionTargetContainerGet(key)
            }
        }
    }

}