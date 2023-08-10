package com.bh.planners.core.kether.compat.virtualentityskillmodule

import com.bh.planners.core.kether.execPlayer
import com.bh.planners.util.safeSync
import com.ipedg.minecraft.virtualentityskillmodule.VesmApi
import org.bukkit.entity.Player
import taboolib.common5.Coerce
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionModuleBind(
    val key: ParsedAction<*>,
    val time: ParsedAction<*>,
    val model: ParsedAction<*>,
    val selector: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(key: String, player: Player, time: Long, model: String) {
        safeSync {
            VesmApi.HasKeyRenderModel(key, player, time, model)
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.newFrame(key).run<Any>().thenAccept { key ->
            frame.newFrame(time).run<Any>().thenAccept { time ->
                frame.newFrame(model).run<Any>().thenAccept { model ->
                    frame.execPlayer(selector!!) {
                        execute(key.toString(), this, Coerce.toLong(time), model.toString())
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }


}