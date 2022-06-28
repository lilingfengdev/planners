package com.bh.planners.core.kether.enhance

import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.library.kether.ArgTypes
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * @author IzzelAliz
 */
class ActionCommand(val command: ParsedAction<*>, val type: Type, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    enum class Type {

        PLAYER, OPERATOR, CONSOLE
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        fun execute(player: Player, type: Type, command: String) {
            when (type) {
                Type.PLAYER -> {
                    player.performCommand(command.replace("@player", player.name))
                }

                Type.OPERATOR -> {
                    val isOp = player.isOp
                    player.isOp = true
                    try {
                        player.performCommand(command.replace("@player", player.name))
                    } catch (ex: Throwable) {
                        ex.printStackTrace()
                    }
                    player.isOp = isOp
                }

                Type.CONSOLE -> {
                    console().performCommand(command.replace("@player", player.name))
                }
            }
        }

        return frame.newFrame(command).run<Any>().thenAcceptAsync({
            val command = it.toString().trimIndent()
            if (selector != null) {
                frame.execPlayer(selector) {
                    execute(this, this@ActionCommand.type, command)
                }
            } else {
                execute(frame.asPlayer() ?: return@thenAcceptAsync, this@ActionCommand.type, command)
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
            ActionCommand(command, by, it.selectorAction())
        }
    }
}