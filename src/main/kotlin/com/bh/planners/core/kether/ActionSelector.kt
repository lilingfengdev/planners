package com.bh.planners.core.kether

import com.bh.planners.api.particle.Demand
import com.bh.planners.core.kether.effect.Target
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
         */
        @KetherParser(["selector"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val key = it.next(ArgTypes.ACTION)
            it.switch {
                case("to", "set") {
                    ActionTargetContainerSet(key, it.next(ArgTypes.ACTION))
                }
                case("remove") {
                    ActionTargetContainerRemove(it.next(ArgTypes.ACTION))
                }
            }
        }
    }


    class ActionTargetContainerSet(val keyAction: ParsedAction<*>, val valueAction: ParsedAction<*>) :
        ScriptAction<Void>() {
        override fun run(frame: ScriptFrame): CompletableFuture<Void> {
            val player = frame.script().sender!!.cast<Player>()
            frame.newFrame(keyAction).run<String>().thenAccept { key ->
                frame.newFrame(valueAction).run<String>().thenAccept { value ->
                    val container = SignTargetContainer(key)
                    val demand = Demand(value)
                    Selector.check(player, frame.getSession(), demand, container)
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