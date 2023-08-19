package com.bh.planners.endtime.kether.virtualentityskillmodule

import com.bh.planners.core.kether.bukkitPlayer
import com.bh.planners.core.kether.execPlayer
import com.ipedg.minecraft.virtualentityskillmodule.VesmApi
import org.bukkit.entity.Player
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionModuleClear(
    val key: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(key: String, player: Player) {
        VesmApi.ClearKeyRenderModel(key, player)
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(key).run<Any>().thenAccept { key ->
            if (selector != null) {
                frame.execPlayer(selector) {
                    execute(key.toString(), this)
                }
            } else {
                execute(key.toString(), frame.bukkitPlayer() ?: return@thenAccept)
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}