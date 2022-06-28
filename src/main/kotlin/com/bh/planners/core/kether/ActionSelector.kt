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
                frame.rootVariables().get<Target.Container>(it.toString()).orElse(Target.Container()).size
            }
        }

    }

    class ActionTargetContainerGet(val action: ParsedAction<*>) : ScriptAction<Target.Container?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container?> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.rootVariables().get<Target.Container>(it.toString()).get()
            }
        }

    }

    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.createTargets(valueAction).thenAccept { container ->
                    frame.rootVariables()[key] = container
                }
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionTargetContainerRemove(val keyAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.rootVariables().remove(key)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionTargetContainerList(val key: ParsedAction<*>) : ScriptAction<Set<Target>>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Set<Target>> {

            return frame.newFrame(key).run<String>().thenApply {
                frame.rootVariables().get<Target.Container>(it.toString()).orElse(Target.Container()).targets
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
            it.switch {
                case("to", "set") {
                    ActionTargetContainerSet(key, it.next(ArgTypes.ACTION))
                }
                case("remove") {
                    ActionTargetContainerRemove(key)
                }
                case("size") {
                    ActionTargetContainerGetSize(key)
                }
                case("list") {
                    ActionTargetContainerList(key)
                }
                other {
                    ActionTargetContainerGet(key)
                }
            }
        }
    }

}