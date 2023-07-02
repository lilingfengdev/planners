package com.bh.planners.core.kether.compat.dragoncore

import com.bh.planners.api.common.SimpleTimeoutTask
import com.bh.planners.core.kether.containerOrSender
import com.bh.planners.core.kether.createContainer
import com.bh.planners.core.kether.readAccept
import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionDragonRopeSend(
    val key: String,
    val path: String,
    val duration: ParsedAction<*>,
    val selector1: ParsedAction<*>,
    val selector2: ParsedAction<*>?,
) : ScriptAction<Void>() {

    fun execute(key: String, path: String, duration: Long, entity1: Entity, entity2: Entity) {
        val rope = Rope(entity1, entity2, path, key)
        RopeRender.rendline[key] = rope
        SimpleTimeoutTask.createSimpleTask(duration, true) {
            remove(key)
        }
    }

    fun remove(key: String) {
        RopeRender.rendline.remove(key)
        Bukkit.getOnlinePlayers().forEach {
            CoreAPI.removePlayerWorldTexture(it, "${key}1")
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.readAccept<Long>(duration) { duration ->
            frame.createContainer(selector1).thenAccept { selector1 ->
                val entity1 = selector1.firstEntityTarget() ?: return@thenAccept
                frame.containerOrSender(selector2).thenAccept { selector2 ->
                    val entity2 = selector2.firstEntityTarget() ?: return@thenAccept
                    execute(key, path, duration, entity1, entity2)
                }
            }
        }
        return CompletableFuture.completedFuture(null)
    }

}