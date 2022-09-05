package com.bh.planners.core.kether

import org.bukkit.Bukkit
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.common5.Coerce
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionWait(val ticks: ParsedAction<*>) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()

        frame.newFrame(ticks).run<Any>().thenAccept {
            val ticks = Coerce.toLong(it)
            val bukkitTask = submit(delay = ticks, async = !Bukkit.isPrimaryThread()) {
                // 如果玩家在等待过程中离线则终止脚本
                if (frame.script().sender?.isOnline() == false) {
                    ScriptService.terminateQuest(frame.script())
                    return@submit
                }
                future.complete(null)
            }
            frame.addClosable(AutoCloseable {
                bukkitTask.cancel()
            })

        }


        return future
    }

    internal object Parser {

        @KetherParser(["wait", "delay", "sleep"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            ActionWait(it.nextParsedAction())
        }
    }
}