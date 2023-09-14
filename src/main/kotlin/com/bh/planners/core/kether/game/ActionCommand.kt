package com.bh.planners.core.kether.game

import com.bh.planners.core.kether.NAMESPACE
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.nextSelectorOrNull
import com.bh.planners.core.kether.read
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
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
            frame.containerOrSender(selector).thenAccept {
                it.forEachPlayer {
                    runCatching { execute(this, this@ActionCommand.type, command) }
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
            ActionCommand(command, by, it.nextSelectorOrNull())
        }
    }
}