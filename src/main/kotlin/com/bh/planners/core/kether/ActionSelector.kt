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
            return frame.newFrame(action).run<String>().thenApply { selector ->
                (frame.getSession().flags[selector]?.asContainer() ?: Target.Container()).size
            }
        }

    }

    class ActionTargetContainerGet(val action: ParsedAction<*>) : ScriptAction<Target.Container?>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Target.Container?> {
            val future = CompletableFuture<Target.Container?>()
            frame.newFrame(action).run<Any>().thenAccept { selector ->
                val session = frame.getSession()
                val data = session.flags[selector.toString()]
                info(data)
                future.complete(data?.asContainer())
            }
            return future
        }

    }

    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                val session = frame.getSession()
                frame.createTargets(valueAction).thenAccept { selector ->
                    session.flags[key] = selector.unsafeData()
                }
            }

            return CompletableFuture.completedFuture(null)
        }
    }

    class ActionTargetContainerRemove(val keyAction: ParsedAction<*>) : ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.getSession().flags.remove(key)
            }
            return CompletableFuture.completedFuture(null)
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
                other {
                    ActionTargetContainerGet(key)
                }
            }
        }
    }

}