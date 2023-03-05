package com.bh.planners.core.kether

import com.bh.planners.core.effect.Target
import taboolib.common.OpenResult
import taboolib.common.platform.function.warning
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

class ActionSelector {

    class ActionTargetContainerGetSize(val action: ParsedAction<*>) : ScriptAction<Int>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.variables().get<Target.Container>(it.toString()).orElseGet { EMPTY_CONTAINER }.size
            }
        }

    }

    class ActionTargetContainerGet(val action: ParsedAction<*>) : ScriptAction<Target.Container?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container?> {
            return frame.newFrame(action).run<Any>().thenApply {
                frame.variables().get<Target.Container>(it.toString()).orElseGet { null }
            }
        }

    }

    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val future = CompletableFuture<Void>()
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.createContainer(valueAction).thenAccept { container ->
                    frame.variables()[key] = container
                    future.complete(null)
                }
            }
            return future
        }
    }

    class ActionTargetContainerRemove(val keyAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.variables().remove(key)
            }
            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionTargetContainerList(val key: ParsedAction<*>) : ScriptAction<Target.Container>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {

            return frame.newFrame(key).run<String>().thenApply {
                frame.variables().get<Target.Container>(it.toString()).orElseGet { EMPTY_CONTAINER }
            }

        }
    }

    class ActionTargetContainerUnmerge(val key: ParsedAction<*>, val value: ParsedAction<*>) :
        ScriptAction<Target.Container>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.run(key).str { key ->
                frame.createContainer(value).thenAccept { container ->
                    val optional = frame.variables().get<Target.Container>(key)
                    if (optional.isPresent) {
                        future.complete(optional.get().also {
                            it.unmerge(container)
                        })
                    }else {
                        warning("Selector $key is not found.")
                        future.complete(container)
                    }
                }
            }
            return future
        }
    }

    class ActionTargetContainerMerge(val key: ParsedAction<*>, val value: ParsedAction<*>) : ScriptAction<Target.Container>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container> {
            val future = CompletableFuture<Target.Container>()
            frame.run(key).str { key ->
                frame.createContainer(value).thenAccept { container ->
                    val optional = frame.variables().get<Target.Container>(key)
                    if (optional.isPresent) {
                        future.complete(optional.get().also {
                            it.merge(container)
                        })
                    }else {
                        warning("Selector $key is not found.")
                        future.complete(container)
                    }
                }
            }
            return future
        }
    }

    companion object {

        val EMPTY_CONTAINER = Target.Container()

        /**
         * 缓存目标容器
         * selector [key: action] to [selector]
         * selector g0 to "-@range 10 10 10"
         *
         * 删除
         * selector [key: action] remove
         *
         * 列表
         * selector [key: action] list
         *
         * 取
         * selector [key: action]
         * selector [key: action] size
         *
         * 合并
         * selector [key: action] merge they ""
         *
         * 拆除
         * selector [key: action] unmerge they ""
         *
         */
        @KetherParser(["selector"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val key = it.nextParsedAction()
            try {
                it.mark()
                when (it.expects("to", "set", "remove", "size", "list", "merge", "unmerge")) {

                    "to", "set" -> {
                        ActionTargetContainerSet(key, it.nextParsedAction())
                    }

                    "remove" -> ActionTargetContainerRemove(key)
                    "size" -> ActionTargetContainerGetSize(key)
                    "list" -> ActionTargetContainerList(key)
                    "get" -> ActionTargetContainerGet(key)
                    "merge" -> ActionTargetContainerMerge(key, it.selectorAction()!!)
                    "unmerge" -> ActionTargetContainerUnmerge(key, it.selectorAction()!!)
                    else -> ActionTargetContainerList(key)
                }
            } catch (_: Exception) {
                it.reset()
                ActionTargetContainerGet(key)
            }
        }


        @KetherProperty(bind = Target.Container::class)
        fun propertyArray() = object : ScriptProperty<Target.Container>("target.container.operator") {

            override fun read(instance: Target.Container, key: String): OpenResult {
                return when {
                    key.isInt() -> OpenResult.successful(instance[key.toInt()])
                    key == "length" || key == "size" -> OpenResult.successful(instance.size)
                    else -> OpenResult.failed()
                }
            }

            override fun write(instance: Target.Container, key: String, value: Any?): OpenResult {
                return if (key.isInt()) {
                    val target = (value as? Target.Container)?.firstTarget()
                    if (target != null) {
                        instance[key.toInt()] = target
                    }
                    OpenResult.successful()
                } else {
                    OpenResult.failed()
                }
            }
        }

    }

}