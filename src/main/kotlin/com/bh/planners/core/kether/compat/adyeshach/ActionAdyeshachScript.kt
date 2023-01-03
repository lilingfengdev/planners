package com.bh.planners.core.kether.compat.adyeshach

import com.bh.planners.core.effect.Target
import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execPlayer
import com.bh.planners.core.kether.runTransfer
import ink.ptms.adyeshach.internal.command.CommandScript
import org.bukkit.Bukkit
import taboolib.common.platform.function.submitAsync
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import taboolib.module.kether.printKetherErrorMessage
import taboolib.module.kether.script
import taboolib.platform.util.sendLang
import java.util.concurrent.CompletableFuture

class ActionAdyeshachScript(val file: ParsedAction<*>, val args: List<ParsedAction<*>>, val selector: ParsedAction<*>?) :
    ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {

        frame.runTransfer<String>(file).thenAccept { file ->
            val array = ArrayList<String>()
            process(frame,0,array)

            if (selector != null) {
                frame.execPlayer(selector) { run(file, this.name, array.toTypedArray()) }
            } else {
                run(file, frame.bukkitPlayer()?.name ?: return@thenAccept, array.toTypedArray())
            }
        }

        return CompletableFuture.completedFuture(null)
    }

    fun process(frame: ScriptFrame,cur: Int,array: ArrayList<String>) {
        if (cur < args.size) {
            frame.newFrame(args[cur]).run<Any>().thenApply {

                if (it is Target.Container) {
                    it.forEachEntity {
                        if (this is AdyeshachEntity) {
                            array.add(this.id)
                        }
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