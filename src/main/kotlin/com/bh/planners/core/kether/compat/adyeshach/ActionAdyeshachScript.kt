package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.compat.adyeshach.ActionAdyeshach.foreachAdyEntity
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.read
import ink.ptms.adyeshach.module.command.CommandScript
import org.bukkit.Bukkit
import taboolib.common.platform.function.submitAsync
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionAdyeshachScript(
    val file: ParsedAction<*>,
    val args: List<ParsedAction<*>>,
    val selector: ParsedAction<*>?,
) :
    ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.read<String>(file).thenAccept { file ->
            val array = ArrayList<String>()
            process(frame, 0, array)

            if (selector != null) {
                frame.execPlayer(selector) { run(file, this.name, array.toTypedArray()) }
            } else {
                run(file, frame.bukkitPlayer()?.name ?: return@thenAccept, array.toTypedArray())
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun process(frame: ScriptFrame, cur: Int, array: ArrayList<String>) {
        if (cur < args.size) {
            frame.newFrame(args[cur]).run<Any>().thenApply {
                if (it is Target.Container) {
                    it.foreachAdyEntity {
                        array += this.id
                    }
                } else {
                    array.add(it.toString())
                    process(frame, cur + 1, array)
                }
            }
        }
    }

    fun run(file: String, viewer: String? = null, args: Array<String> = emptyArray()) {
        submitAsync {
            CommandScript.commandRun(Bukkit.getConsoleSender(), file, viewer, args)
        }
    }

}