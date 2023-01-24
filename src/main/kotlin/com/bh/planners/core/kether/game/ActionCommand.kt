package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.*
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.submitAsync
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

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.read<String>(command).thenAccept { command ->
            if (selector != null) {
                frame.createContainer(selector).thenAccept { container ->
                    submitAsync {
                        container.forEachPlayer {
                            execute(this, this@ActionCommand.type, command)
                        }
                    }
                }
            } else {
                submit {
                    execute(frame.bukkitPlayer() ?: return@submit, this@ActionCommand.type, command)
                }
            }
        }

        return CompletableFuture.completedFuture(null)


    }

    internal object Parser {

        @KetherParser(["command"], namespace = NAMESPACE, shared = true)
        fun parser() = scriptParser {
            val command = it.nextParsedAction()
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