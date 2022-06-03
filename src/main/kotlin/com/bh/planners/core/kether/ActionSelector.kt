package com.bh.planners.core.kether

import com.bh.planners.api.particle.Demand
import com.bh.planners.core.kether.effect.Target
import com.bh.planners.core.kether.effect.Target.Companion.createContainer
import com.bh.planners.core.kether.selector.Selector
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
import taboolib.common.platform.Schedule
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.*
import java.util.concurrent.CompletableFuture

class ActionSelector {

    companion object {

        private val sessions = Collections.synchronizedMap(mutableMapOf<Session, MutableList<SignTargetContainer>>())

        fun getContainer(session: Session, key: String): SignTargetContainer? {
            val list = sessions[session] ?: emptyList()
            return list.firstOrNull { it.name == key }
        }

        fun add(session: Session, container: SignTargetContainer) {
            sessions.computeIfAbsent(session) { mutableListOf() } += container
        }

        private val lock = Any()

        @Schedule(period = 60 * 20)
        fun timer() {
            val removeList = sessions.keys.filter { it.closed }
            synchronized(lock) {
                removeList.forEach {
                    sessions.remove(it)
                }
            }
        }

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

    class ActionTargetContainerGetSize(val action: ParsedAction<*>) : ScriptAction<Int>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Int> {
            return frame.newFrame(action).run<String>().thenApply { selector ->
                (getContainer(frame.getSession(), selector) ?: Target.Container()).size
            }
        }

    }

    class ActionTargetContainerGet(val action: ParsedAction<*>) : ScriptAction<List<String>>() {
        override fun run(frame: ScriptFrame): CompletableFuture<List<String>> {
            return frame.newFrame(action).run<String>().thenApply { selector ->
                val container = getContainer(frame.getSession(), selector) ?: Target.Container()
                container.targets.map { it.toLocal() }
            }
        }

    }

    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.newFrame(valueAction).run<String>().thenAccept { value ->
                    val container = SignTargetContainer(key)
                    val demand = Demand(value)
                    Selector.check(frame.toOriginLocation(), frame.getSession(), demand, container)
                    add(frame.getSession(), container)
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

    class SignTargetContainer(val name: String) : Target.Container()

}