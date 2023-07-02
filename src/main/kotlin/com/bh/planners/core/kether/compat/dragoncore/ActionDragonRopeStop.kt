package com.bh.planners.core.kether.compat.dragoncore

import eos.moe.dragoncore.api.CoreAPI
import org.bukkit.Bukkit
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture


class ActionDragonRopeStop(
    val key: String,
) : ScriptAction<Void>() {

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        Bukkit.getOnlinePlayers().forEach {
            RopeRender.rendline.remove(key)
            CoreAPI.removePlayerWorldTexture(it, "${key}1")
        }
        return CompletableFuture.completedFuture(null)
    }

}