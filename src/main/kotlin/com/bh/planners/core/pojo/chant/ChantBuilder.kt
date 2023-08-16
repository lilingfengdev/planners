package com.bh.planners.core.pojo.chant

import com.bh.planners.api.common.Id
import com.bh.planners.api.event.PlayerChantEvents
import com.bh.planners.core.pojo.Session
import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.inject.ClassVisitor
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.cdouble
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

abstract class ChantBuilder(val duration: Long, val period: Long, val delay: Long, val async: Boolean) {

    abstract fun build(sender: Player, tags: List<Interrupt>, context: Session, onTick: (progress: Double) -> Unit): CompletableFuture<Void>

    fun createProgress(process: Process, duration: Long, delay: Long, period: Long, async: Boolean = true, block: (Double) -> Unit): CompletableFuture<Void> {
        var index = duration / period
        return createProgress(process) { future ->
            submit(async = async, period = period, delay = delay) {
                // 如果进程被其他程序终止 或者到最后一帧
                if (process.actionBreak || index-- <= 0) {
                    cancel()
                    future.complete(null)
                    return@submit
                }
                block(((duration / period) - index.cdouble) / (duration / period))
            }
        }
    }

    fun createProgress(process: Process, block: (CompletableFuture<Void>) -> Unit): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        chanting[process.sender] = process
        PlayerChantEvents.Start(process.sender, process).call()
        block(future)

        return future.thenAccept {
            chanting.remove(process.sender)
            PlayerChantEvents.Stop(process.sender, process).call()
        }
    }

    companion object {

        private val builders = mutableMapOf<String, Class<ChantBuilder>>()

        private val chanting = mutableMapOf<Player, Process>()

        fun newInstance(id: String, vararg args: Any): ChantBuilder {
            return builders[id]?.invokeConstructor(*args) ?: error("Couldn't find chant builder '$id'")
        }

        fun getRunningProcessOrNull(player: Player): Process? {
            return chanting[player]
        }

        fun registerBuilder(id: String, clazz: Class<ChantBuilder>) {
            builders[id] = clazz
        }

        @Awake
        object Visitor : ClassVisitor(0) {

            override fun getLifeCycle(): LifeCycle {
                return LifeCycle.LOAD
            }

            @Suppress("UNCHECKED_CAST")
            override fun visitEnd(clazz: Class<*>, instance: Supplier<*>?) {
                if (ChantBuilder::class.java.isAssignableFrom(clazz) && clazz.isAnnotationPresent(Id::class.java)) {
                    registerBuilder(clazz.getAnnotation(Id::class.java).id, clazz as Class<ChantBuilder>)
                }
            }

        }

    }

}