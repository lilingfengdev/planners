package com.bh.planners.core.kether.compat.germplugin

import com.bh.planners.core.kether.execEntity
import com.germ.germplugin.api.GermPacketAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.common.platform.function.info
import taboolib.library.kether.ParsedAction
import taboolib.module.kether.ScriptAction
import taboolib.module.kether.ScriptFrame
import java.util.concurrent.CompletableFuture

class ActionGermAnimation(val state: String, val remove: Boolean, val selector: ParsedAction<*>) : ScriptAction<Void>() {

    fun execute(entity: Entity, state: String, remove: Boolean) {
        Bukkit.getOnlinePlayers().forEach {
            if (remove) {
                GermPacketAPI.stopModelAnimation(it, entity.entityId, state)
            } else {
                GermPacketAPI.sendModelAnimation(it, entity.entityId, state)
            }
        }
    }

    override fun run(frame: ScriptFrame): CompletableFuture<Void> {
        frame.execEntity(selector) { execute(this, state, remove) }
        return CompletableFuture.completedFuture(null)
    }
}