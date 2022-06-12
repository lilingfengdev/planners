package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.createTargets
import com.bh.planners.core.kether.executor
import taboolib.common.platform.function.console
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: ParsedAction<*>, val type: Type, val selector: ParsedAction<*>) :
    ScriptAction<Void>() {

    enum class Type {

        PLAYER, OPERATOR, CONSOLE
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        return frame.newFrame(command).run<Any>().thenAcceptAsync({
            val command = it.toString().trimIndent()
            frame.createTargets(selector).thenAccept { container ->
                when (type) {
                    Type.PLAYER -> {
                        container.forEachPlayer { performCommand(command.replace("@player", name)) }
                    }
                    Type.OPERATOR -> {
                        container.forEachPlayer {
                            val isOp = isOp
                            this.isOp = true
                            try {
                                performCommand(command.replace("@player", name))
                            } catch (ex: Throwable) {
                                ex.printStackTrace()
                            }
                            this.isOp = isOp
                        }
                    }
                    Type.CONSOLE -> {
                        container.forEachPlayer {
                            console().performCommand(command.replace("@player", name))
                        }
                    }
                }

            }


        }, frame.context().executor)
    }

    internal object Parser {

        @KetherParser(["command"], namespace = NAMESPACE)
        fun parser() = scriptParser {
            val command = it.next(ArgTypes.ACTION)
            it.mark()
            val by = try {
                it.expects("by", "with", "as")
                when (val type = it.nextToken()) {
                    "player" -> Type.PLAYER
                    "op", "operator" -> Type.OPERATOR
                    "console", "server" -> Type.CONSOLE
                    else -> throw KetherError.NOT_COMMAND_SENDER.create(type)
                }
            } catch (ignored: Exception) {
                it.reset()
                Type.PLAYER
            }
            ActionCommand(command, by, it.next(ArgTypes.ACTION))
        }
    }
}